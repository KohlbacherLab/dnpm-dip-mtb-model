package de.dnpm.dip.mtb.model


import java.time.LocalDate
import de.dnpm.dip.coding.{
  Coding,
  CodedEnum,
  CodeSystem,
  DefaultCodeSystem,
}
import de.dnpm.dip.coding.atc.ATC
import de.dnpm.dip.model.{
  Id,
  Period,
  Reference,
  Patient,
  Therapy,
  MedicationTherapy
}
import play.api.libs.json.{
  Json,
  Format,
  OFormat
}


final case class MTBMedicationTherapy
(
  id: Id[MTBMedicationTherapy],
  patient: Reference[Patient],
  indication: Reference[MTBDiagnosis],
  therapyLine: Option[Int],
  basedOn: Option[Reference[MTBMedicationRecommendation]],
  recordedOn: LocalDate,
  status: Coding[Therapy.Status.Value],
  statusReason: Option[Coding[Therapy.StatusReason.Value]],
  period: Option[Period[LocalDate]],
  medication: Option[Set[Coding[ATC]]],
  note: Option[String]
)
extends MedicationTherapy[ATC]
{
  val category = None
}


object MTBMedicationTherapy
{

/*
  object StatusReason
  {

    val PaymentRefused      = "payment-refused"
    val PaymentPending      = "payment-pending"
    val PaymentEnded        = "payment-ended"
    val NoIndication        = "no-indication"
    val MedicalReason       = "medical-reason"
    val PatientRefusal      = "patient-refusal"
    val PatientWish         = "patient-wish"
    val PatientDeath        = "patient-death"
    val LostToFU            = "lost-to-fu"
    val Remission           = "chronic-remission"
    val Progression         = "progression"
    val Toxicity            = "toxicity"
    val OtherTherapyChosen  = "other-therapy-chosen"
    val ContinuedExternally = "continued-externally"
    val StateDeterioration  = "deterioration"
    val Other               = "other"
    val Unknown             = "unknown"
 

    implicit val codeSystem: CodeSystem[Therapy.StatusReason] =
      CodeSystem[Therapy.StatusReason](
        uri = Coding.System[Therapy.StatusReason].uri,
        name = "Therapy-StatusReason",
        title = Some("Therapy-StatusReason"),
        version = None,
        PaymentRefused      -> "Kostenübernahme abgelehnt",
        PaymentPending      -> "Kostenübernahme noch ausstehend",
        PaymentEnded        -> "Ende der Kostenübernahme",
        NoIndication        -> "Klinisch keine Indikation",
        MedicalReason       -> "Medizinische Gründe",
        PatientRefusal      -> "Therapie durch Patient abgelehnt",
        PatientWish         -> "Auf Wunsch des Patienten",
        PatientDeath        -> "Tod",
        LostToFU            -> "Lost to follow-up",
        Remission           -> "Anhaltende Remission",
        Progression         -> "Progression",
        Toxicity            -> "Toxizität",
        OtherTherapyChosen  -> "Wahl einer anderen Therapie durch Behandler",
        ContinuedExternally -> "Weiterbehandlung extern",
        StateDeterioration  -> "Zustandsverschlechterung",
        Other               -> "Weitere Gründe",
        Unknown             -> "Unbekannt"
      )

  }
*/

  implicit val format: OFormat[MTBMedicationTherapy] =
    Json.format[MTBMedicationTherapy]

}

