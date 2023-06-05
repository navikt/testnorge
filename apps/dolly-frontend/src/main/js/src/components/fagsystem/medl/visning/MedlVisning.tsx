import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import styled from 'styled-components'
import { Medlemskapsperioder } from '@/components/fagsystem/medl/visning/Visning'
import { formatDate, oversettBoolean, showKodeverkLabel } from '@/utils/DataFormatter'
import { MedlKodeverk } from '@/components/fagsystem/medl/MedlConstants'

type Props = {
	medlemskapsperiode: Medlemskapsperioder
}

const H4 = styled.h4`
	width: 100%;
`

export default ({ medlemskapsperiode }: Props) => (
	<div className="person-visning_content">
		<TitleValue
			title="Kilde"
			value={showKodeverkLabel(MedlKodeverk.KILDE, medlemskapsperiode.sporingsinformasjon?.kilde)}
		/>
		<TitleValue
			title="Dekning"
			value={showKodeverkLabel(MedlKodeverk.PERIODE_DEKNING, medlemskapsperiode.dekning)}
		/>
		<TitleValue title="Fra og med" value={formatDate(medlemskapsperiode.fraOgMed)} />
		<TitleValue title="Til og med" value={formatDate(medlemskapsperiode.tilOgMed)} />
		<TitleValue
			title="Grunnlag"
			value={showKodeverkLabel(MedlKodeverk.GRUNNLAG, medlemskapsperiode.grunnlag)}
		/>
		<TitleValue title="Har helsedel" value={oversettBoolean(medlemskapsperiode.helsedel)} />
		<TitleValue
			title="Lovvalg"
			value={showKodeverkLabel(MedlKodeverk.LOVVALG_PERIODE, medlemskapsperiode.lovvalg)}
		/>
		<TitleValue
			title="Lovvalgsland"
			value={showKodeverkLabel(MedlKodeverk.LANDKODER, medlemskapsperiode.lovvalgsland)}
		/>
		<TitleValue title="Aktiv medlem" value={oversettBoolean(medlemskapsperiode.medlem)} />
		<TitleValue
			title="Status"
			value={showKodeverkLabel(MedlKodeverk.PERIODE_STATUS, medlemskapsperiode.status)}
		/>
		<TitleValue
			title="Statusårsak"
			value={showKodeverkLabel(MedlKodeverk.PERIODE_ST_AARSAK, medlemskapsperiode.statusaarsak)}
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
			title="Studieland"
			value={showKodeverkLabel(
				MedlKodeverk.LANDKODER,
				medlemskapsperiode.studieinformasjon?.studieland
			)}
		/>
		<TitleValue
			title="Statsborgerland"
			value={showKodeverkLabel(
				MedlKodeverk.LANDKODER,
				medlemskapsperiode.studieinformasjon?.statsborgerland
			)}
		/>
	</div>
)
