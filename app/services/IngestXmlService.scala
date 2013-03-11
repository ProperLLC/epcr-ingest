package services

import xml.{NodeSeq, Elem}

/**
 * Created with IntelliJ IDEA.
 * User: terry
 * Date: 3/10/13
 * Time: 6:33 PM
 *
 * This class will simply parse the Incident XML into JSON.
 */
object IngestXmlService {

  // Eventually, we can read this from the db or a config file, for not let's just code it to get things rolling
  val nestedElements = Seq(
    "lead12", "gcs1", "gcs2", "als", "arrestwitness", "bls", "complaint", "arrestcpr", "dde", "destdecision", "loc", "mlapss", "moi",
    "patientageunit", "patient_insurance", "arrest", "pupils", "transportcrit", "resp", "severity", "skin", "transfer", "transporttype",
    "vs", "unit1", "unit2", "unittransport", "medhist", "incident", "patient"

  )
  val rootElements = Seq(
    "asst_invl", "bhidcode", "basentfdcode", "cpap_time", "broselowtapecolor", "cardiacarrest_rigor", "code3", "initial_pain", "etco2", "transporthospbpcode",
    "incidentnumber", "jurisdictionstation", "nka", "nosed", "runtype_no", "offpedcolortape", "page2", "patientage", "patientageunit_d", "patientageunit_m",
    "patientageunit_h", "patientageunit_y", "patientageapprox", "patientdob", "patientgender_f", "patientgender_m", "ptnumber", "patientphonenumber_1",
    "patientphonenumber_2", "patientphonenumber_3", "patientfirstname", "patientmiddlename", "patientlastname", "patientssn", "released", "pttoal",
    "patientweight", "patientweightunit_k", "patientweightunit_l", "incidentdate", "documentidnumber", "poisoncontrol", "transporthospcode", "specialstudies",
    "specialstudies", "suspectabuse", "suspectabusereportedto", "suspectbarrier", "suspectetoh", "suspectdrugs", "crewid1", "crewid2", "crewid3", "crewid4",
    "crewid5", "vallium", "vallium_given", "vallium_wasted", "morphine", "morphine_given", "morphine_wasted", "unit_milage", "protocol", "protocol2",
    "complaint_neurovasb_b", "comlaint_neurobasb_p", "medications_none", "cpap_cmh2o", "location_citycode", "unit_milage_fractional", "homeless", "comments"
  )

  def parseIncident( payload : NodeSeq) : Map[String, Any] = {
    // grab all root level fields
    val rootElemMap = rootElements.map( element => (payload \\ element).map(node => (node.label -> node.text))).flatten.toMap
    // grab each element grouping
    val nestedMap = nestedElements.map { elem =>
      elem -> ((payload \\ "_").filter(_.label.startsWith(elem + "_")).map( node=>
        node.label.substring(node.label.lastIndexOf("_") +1)  -> node.text).toMap).toMap
    }
    // returns a Map[String, Map[String,String]]
    // ie. Map(lead12 -> Map(pacer -> "", faxed -> "", time -> "", wavy -> "", artifact -> ""), gcs -> Map(), als -> Map(iogauge -> "", tcpacingtm -> "", ivgauge -> "", intraosstm -> "", glucoselevel2 -> "", ivtm -> "", vagaltm -> "", foreignbodytm -> "", thoratm -> "", 12lead -> "", glucoselevel -> "", glucosetm -> ""), arrestwitness -> Map(citizen -> "", ems -> "", tm -> ""))
    // get the two maps mixed together
    rootElemMap ++ nestedMap
  }
}
