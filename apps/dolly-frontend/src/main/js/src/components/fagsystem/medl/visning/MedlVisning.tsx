import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, oversettBoolean } from '@/utils/DataFormatter'
import { MedlKodeverk } from '@/components/fagsystem/medl/MedlConstants'
import { Medlemskapsperiode } from '@/components/fagsystem/medl/MedlTypes'

type Props = {
	medlemskapsperiode: Medlemskapsperiode
}

export default ({ medlemskapsperiode }: Props) => (
	<div className="person-visning_content">
		<TitleValue
			title="Kilde"
			value={medlemskapsperiode.sporingsinformasjon?.kilde}
			kodeverk={MedlKodeverk.KILDE}
		/>
		<TitleValue
			title="Kildedokument"
			value={medlemskapsperiode.sporingsinformasjon?.kildedokument}
			kodeverk={MedlKodeverk.KILDE_DOK}
		/>
		<TitleValue title="Fra og med" value={formatDate(medlemskapsperiode.fraOgMed)} />
		<TitleValue title="Til og med" value={formatDate(medlemskapsperiode.tilOgMed)} />
		<TitleValue
			title="Grunnlag"
			value={medlemskapsperiode.grunnlag}
			kodeverk={MedlKodeverk.GRUNNLAG}
		/>
		<TitleValue
			title="Dekning"
			value={medlemskapsperiode.dekning}
			kodeverk={MedlKodeverk.PERIODE_DEKNING}
		/>
		<TitleValue
			title="Lovvalgsland"
			value={medlemskapsperiode.lovvalgsland}
			kodeverk={MedlKodeverk.LANDKODER}
		/>
		<TitleValue
			title="Lovvalg"
			value={medlemskapsperiode.lovvalg}
			kodeverk={MedlKodeverk.LOVVALG_PERIODE}
		/>
		<TitleValue
			title="Statsborgerland"
			value={medlemskapsperiode.studieinformasjon?.statsborgerland}
			kodeverk={MedlKodeverk.LANDKODER}
		/>
		<TitleValue
			title="Studieland"
			value={medlemskapsperiode.studieinformasjon?.studieland}
			kodeverk={MedlKodeverk.LANDKODER}
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
			value={medlemskapsperiode.status}
			kodeverk={MedlKodeverk.PERIODE_STATUS}
		/>
		<TitleValue
			title="Statusårsak"
			value={medlemskapsperiode.statusaarsak}
			kodeverk={MedlKodeverk.PERIODE_ST_AARSAK}
		/>
		<TitleValue title="Har helsedel" value={oversettBoolean(medlemskapsperiode.helsedel)} />
		<TitleValue title="Er aktiv medlem" value={oversettBoolean(medlemskapsperiode.medlem)} />
	</div>
)
