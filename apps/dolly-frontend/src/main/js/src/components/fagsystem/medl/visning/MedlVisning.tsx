import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, oversettBoolean, showKodeverkLabel } from '@/utils/DataFormatter'
import { MedlKodeverk } from '@/components/fagsystem/medl/MedlConstants'
import { Medlemskapsperiode } from '@/components/fagsystem/medl/MedlTypes'

type Props = {
	medlemskapsperiode: Medlemskapsperiode
}

export default ({ medlemskapsperiode }: Props) => (
	<div className="person-visning_content">
		<TitleValue
			title="Kilde"
			value={showKodeverkLabel(MedlKodeverk.KILDE, medlemskapsperiode.sporingsinformasjon?.kilde)}
		/>
		<TitleValue
			title="Kildedokument"
			value={showKodeverkLabel(
				MedlKodeverk.KILDE_DOK,
				medlemskapsperiode.sporingsinformasjon?.kildedokument,
			)}
		/>
		<TitleValue title="Fra og med" value={formatDate(medlemskapsperiode.fraOgMed)} />
		<TitleValue title="Til og med" value={formatDate(medlemskapsperiode.tilOgMed)} />
		<TitleValue
			title="Grunnlag"
			value={showKodeverkLabel(MedlKodeverk.GRUNNLAG, medlemskapsperiode.grunnlag)}
		/>
		<TitleValue
			title="Dekning"
			value={showKodeverkLabel(MedlKodeverk.PERIODE_DEKNING, medlemskapsperiode.dekning)}
		/>
		<TitleValue
			title="Lovvalgsland"
			value={showKodeverkLabel(MedlKodeverk.LANDKODER, medlemskapsperiode.lovvalgsland)}
		/>
		<TitleValue
			title="Lovvalg"
			value={showKodeverkLabel(MedlKodeverk.LOVVALG_PERIODE, medlemskapsperiode.lovvalg)}
		/>
		<TitleValue
			title="Statsborgerland"
			value={showKodeverkLabel(
				MedlKodeverk.LANDKODER,
				medlemskapsperiode.studieinformasjon?.statsborgerland,
			)}
		/>
		<TitleValue
			title="Studieland"
			value={showKodeverkLabel(
				MedlKodeverk.LANDKODER,
				medlemskapsperiode.studieinformasjon?.studieland,
			)}
		/>
		<TitleValue
			title="Er delstudie"
			value={oversettBoolean(medlemskapsperiode.studieinformasjon?.delstudie)}
		/>
		<TitleValue
			title="Er søknad innvilget"
			value={oversettBoolean(medlemskapsperiode.studieinformasjon?.soeknadInnvilget)}
		/>
		<TitleValue
			title="Status"
			value={showKodeverkLabel(MedlKodeverk.PERIODE_STATUS, medlemskapsperiode.status)}
		/>
		<TitleValue
			title="Statusårsak"
			value={showKodeverkLabel(MedlKodeverk.PERIODE_ST_AARSAK, medlemskapsperiode.statusaarsak)}
		/>
		<TitleValue title="Har helsedel" value={oversettBoolean(medlemskapsperiode.helsedel)} />
		<TitleValue title="Er aktiv medlem" value={oversettBoolean(medlemskapsperiode.medlem)} />
	</div>
)
