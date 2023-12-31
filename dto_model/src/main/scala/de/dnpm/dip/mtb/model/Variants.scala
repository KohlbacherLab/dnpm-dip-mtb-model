package de.dnpm.dip.mtb.model


import java.net.URI
import java.time.LocalDate
import cats.Applicative
import de.dnpm.dip.model.{
  Id,
  ExternalId,
  Patient,
  Reference,
  Quantity,
  UnitOfMeasure
}
import de.dnpm.dip.coding.{
  Coding,
  CodeSystem,
  CodedEnum,
  DefaultCodeSystem,
  CodeSystemProvider,
  CodeSystemProviderSPI
}
import de.dnpm.dip.coding.hgnc.HGNC
import de.dnpm.dip.coding.hgvs.HGVS
import play.api.libs.json.{
  Json,
  Format,
  OFormat,
  Reads
}



sealed abstract class Variant
{
  val id: Id[Variant]
  val patient: Reference[Patient]
}

object Variant
{

  final case class PositionRange
  (
    start: Long,
    end: Option[Long]
  )

  object PositionRange
  {
    implicit val format: OFormat[PositionRange] =
      Json.format[PositionRange]
  }


  private def symbol(
    coding: Coding[HGNC]
  )(  
    implicit hgnc: CodeSystem[HGNC]
  ): Option[String] = {

    import HGNC.extensions._

    hgnc.concept(coding.code)
      .map(_.symbol)
  }


  def display(
    variant: Variant
  )(
    implicit hgnc: CodeSystem[HGNC]
  ): String =
    variant match { 

      case snv: SNV =>
        s"SNV ${snv.gene.flatMap(symbol).getOrElse("")} ${snv.proteinChange.map(c => c.display.getOrElse(c.code.value)).getOrElse("")}"

      case cnv: CNV =>
        s"CNV ${cnv.reportedAffectedGenes.flatMap(symbol).mkString(",")} ${cnv.`type`.display.getOrElse("")}"

      case DNAFusion(_,_,partner5pr,partner3pr,_) =>
        s"DNA-Fusion ${symbol(partner5pr.gene).getOrElse("N/A")}-${symbol(partner3pr.gene).getOrElse("N/A")}"
      
      case RNAFusion(_,_,partner5pr,partner3pr,_) =>
        s"RNA-Fusion ${symbol(partner5pr.gene).getOrElse("N/A")}-${symbol(partner3pr.gene).getOrElse("N/A")}"
      
      case rnaSeq: RNASeq =>
        s"RNA-Seq ${rnaSeq.gene.flatMap(symbol).getOrElse("N/A")}"

    }


  // Type class to check equivalence of variants,
  // i.e. if 2 variant object are conceptually the same variant 
  // irrespective of the patient reference on the object or interpretation values
  sealed trait Eq[T <: Variant] extends ((T,T) => Boolean)

  object Eq 
  {

    def apply[T <: Variant](implicit eq: Eq[T]) = eq

    private def instance[T <: Variant](f: (T,T) => Boolean): Eq[T] =
      new Eq[T]{
        override def apply(v1: T, v2: T) = f(v1,v2)
      }

    implicit val snvEq: Eq[SNV] =
      instance(
        (v1, v2) =>
          v1.chromosome == v2.chromosome &&
          v1.gene == v2.gene &&
          v1.dnaChange == v2.dnaChange &&
          v1.proteinChange == v2.proteinChange
      )
  
    implicit val cnvEq: Eq[CNV] =
      instance(
        (v1, v2) =>
          v1.chromosome == v2.chromosome &&
          v1.reportedAffectedGenes == v2.reportedAffectedGenes &&
          v1.`type` == v2.`type`
      )

    implicit def fusionEq[F <: Fusion[_ <: { def gene: Coding[HGNC] }]]: Eq[F] =
      instance {
        (v1, v2) =>
          import scala.language.reflectiveCalls

          v1.fusionPartner5pr.gene == v2.fusionPartner5pr.gene &&
          v1.fusionPartner3pr.gene == v2.fusionPartner3pr.gene
      }

    implicit val rnaSeqEq: Eq[RNASeq] =
      instance(
        (v1, v2) =>
          v1.gene == v2.gene
      )

    implicit class syntax[T <: Variant](variant: T)(implicit eq: Eq[T])
    {
      def ===(other: T): Boolean =
        eq(variant,other)
    }

  }

}

object Chromosome
extends CodedEnum("chromosome")
with DefaultCodeSystem
{
  val chr1,
      chr2,
      chr3,
      chr4,
      chr5,
      chr6,
      chr7,
      chr8,
      chr9,
      chr10,
      chr11,
      chr12,
      chr13,
      chr14,
      chr15,
      chr16,
      chr17,
      chr18,
      chr19,
      chr21,
      chr22,
      chrX,
      chrY = Value


  override val display = {
    case chr => chr.toString
  }

  implicit val format: Format[Chromosome.Value] =
    Json.formatEnum(this)
}


sealed trait dbSNP
object dbSNP
{
  implicit val codingSystem =
    Coding.System[dbSNP]("https://www.ncbi.nlm.nih.gov/snp/")
}

sealed trait COSMIC
object COSMIC
{
  implicit val codingSystem =
    Coding.System[COSMIC]("https://cancer.sanger.ac.uk/cosmic")
}

sealed trait ClinVar
object ClinVar
{
  implicit val codingSystem =
    Coding.System[ClinVar]("https://www.ncbi.nlm.nih.gov/clinvar/")

  implicit val codeSystem: CodeSystem[ClinVar] =
    CodeSystem(
      name = "ClinVar-Interpretation",
      title = Some("ClinVar Interpretation"),
      version = None,
      "0" -> "Not Applicable",
      "1" -> "Benign",
      "2" -> "Likely benign",
      "3" -> "Uncertain significance",
      "4" -> "Likely pathogenic",
      "5" -> "Pathogenic"
    )
}

sealed trait Transcript

final case class SNV
(
  id: Id[Variant],
  patient: Reference[Patient],
  externalIds: Set[ExternalId[SNV]],    // dbSNPId or COSMIC ID to be listed here
  chromosome: Coding[Chromosome.Value],
  gene: Option[Coding[HGNC]],
  transcriptId: ExternalId[Transcript],
  position: Variant.PositionRange,
  altAllele: SNV.Allele,
  refAllele: SNV.Allele,
  dnaChange: Option[Coding[HGVS]],
  proteinChange: Option[Coding[HGVS]],
  readDepth: SNV.ReadDepth,
  allelicFrequency: SNV.AllelicFrequency,
  interpretation: Option[Coding[ClinVar]]
)
extends Variant

object SNV
{

  final case class Allele(value: String) extends AnyVal
  final case class AllelicFrequency(value: Double) extends AnyVal
  final case class ReadDepth(value: Int) extends AnyVal

  implicit val formatAllele: Format[Allele] =
    Json.valueFormat[Allele]

  implicit val formatAllelicFreq: Format[AllelicFrequency] =
    Json.valueFormat[AllelicFrequency]

  implicit val formatReadDepth: Format[ReadDepth] =
    Json.valueFormat[ReadDepth]

  implicit val format: OFormat[SNV] =
    Json.format[SNV]
}


final case class CNV
(
  id: Id[Variant],
  patient: Reference[Patient],
  chromosome: Coding[Chromosome.Value],
  startRange: Option[Variant.PositionRange],
  endRange: Option[Variant.PositionRange],
  totalCopyNumber: Option[Int],
  relativeCopyNumber: Option[Double],
  cnA: Option[Double],
  cnB: Option[Double],
  reportedAffectedGenes: Set[Coding[HGNC]],
  reportedFocality: Option[String],
  `type`: Coding[CNV.Type.Value],
  copyNumberNeutralLoH: Set[Coding[HGNC]],
)
extends Variant


object CNV
{
  object Type
  extends CodedEnum("dnpm-dip/mtb/ngs-report/cnv/type")
  with DefaultCodeSystem
  {
    val LowLevelGain  = Value("low-level-gain")
    val HighLevelGain = Value("high-level-gain")
    val Loss          = Value("loss")

    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }

  }

  implicit val format: OFormat[CNV] =
    Json.format[CNV]
}



sealed abstract class Fusion[
  Partner <: { def gene: Coding[HGNC] }
]
extends Variant
{
  val fusionPartner5pr: Partner
  val fusionPartner3pr: Partner
  val reportedNumReads: Int
}

final case class DNAFusion
(
  id: Id[Variant],
  patient: Reference[Patient],
  fusionPartner5pr: DNAFusion.Partner,
  fusionPartner3pr: DNAFusion.Partner,
  reportedNumReads: Int
)
extends Fusion[DNAFusion.Partner]

object DNAFusion
{

  final case class Partner
  (
    chromosome: Chromosome.Value,
    position: Long,
    gene: Coding[HGNC]
  )

  implicit val formatPartner: OFormat[Partner] =
    Json.format[Partner]

  implicit val format: OFormat[DNAFusion] =
    Json.format[DNAFusion]
}


final case class RNAFusion
(
  id: Id[Variant],
  patient: Reference[Patient],
  fusionPartner5pr: RNAFusion.Partner,
  fusionPartner3pr: RNAFusion.Partner,
  reportedNumReads: Int
)
extends Fusion[RNAFusion.Partner]

object RNAFusion
{

  object Strand extends Enumeration
  {
    val Plus  = Value("+")
    val Minus = Value("-")

    implicit val format =
      Json.formatEnum(this)
  }

  final case class Partner
  (
    ids: Set[ExternalId[_]],  // Transcript ID and Exon Id listed here
    position: Long,
    gene: Coding[HGNC],
    strand: Strand.Value
  )

  implicit val formatPartner: OFormat[Partner] =
    Json.format[Partner]

  implicit val format: OFormat[RNAFusion] =
    Json.format[RNAFusion]
}


final case class RNASeq
(
  id: Id[Variant],
  patient: Reference[Patient],
  ids: Set[ExternalId[_]],    // Entrez ID, Ensembl ID or Transcript ID to be listed here
  gene: Option[Coding[HGNC]],
  fragments: RNASeq.Fragments,
  fromNGS: Boolean,
  tissueCorrectedExpression: Boolean,
  rawCounts: Int,
  librarySize: Int,
  cohortRanking: Option[Int]
)
extends Variant

object RNASeq
{

  val fragmentsPerKbMillion =
    UnitOfMeasure("Fragments per Kb Million","fragments/Kb Million")

  final case class Fragments(value: Double) extends Quantity 
  {
    override val unit = fragmentsPerKbMillion
  }

  implicit val readsFragments: Reads[Fragments] =
    Json.reads[Fragments]

  implicit val format: OFormat[RNASeq] =
    Json.format[RNASeq]
}

