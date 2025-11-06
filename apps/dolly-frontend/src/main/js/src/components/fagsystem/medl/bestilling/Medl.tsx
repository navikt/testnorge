import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingData, BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { MedlKodeverk } from '@/components/fagsystem/medl/MedlConstants'
import { formatDate, oversettBoolean, showLabel } from '@/utils/DataFormatter'
import { Medlemskapsperiode } from '@/components/fagsystem/medl/MedlTypes'

type MedlProps = {
	medl: Medlemskapsperiode
}

export const Medl = ({ medl }: MedlProps) => {
	if (!medl || isEmpty(medl)) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Medlemskap (MEDL)</BestillingTitle>
				<BestillingData>
					<TitleValue title="Kilde" value={showLabel('medlKilder', medl?.kilde)} />
					<TitleValue
						title="Kildedokument"
						value={medl?.kildedokument}
						kodeverk={MedlKodeverk.KILDE_DOK}
					/>
					<TitleValue title="F.o.m. dato" value={formatDate(medl?.fraOgMed)} />
					<TitleValue title="T.o.m. dato" value={formatDate(medl?.tilOgMed)} />
					<TitleValue title="Grunnlag" value={medl?.grunnlag} kodeverk={MedlKodeverk.GRUNNLAG} />
					<TitleValue
						title="Dekning"
						value={medl?.dekning}
						kodeverk={MedlKodeverk.PERIODE_DEKNING}
					/>
					<TitleValue
						title="Lovvalg"
						value={medl?.lovvalg}
						kodeverk={MedlKodeverk.LOVVALG_PERIODE}
					/>
					<TitleValue
						title="Lovvalgsland"
						value={medl?.lovvalgsland}
						kodeverk={MedlKodeverk.LANDKODER}
					/>
					<TitleValue
						title="Statsborgerland"
						value={medl?.studieinformasjon?.statsborgerland}
						kodeverk={MedlKodeverk.LANDKODER}
					/>
					<TitleValue
						title="Studieland"
						value={medl?.studieinformasjon?.studieland}
						kodeverk={MedlKodeverk.LANDKODER}
					/>
					<TitleValue
						title="Er delstudie"
						value={oversettBoolean(medl?.studieinformasjon?.delstudie)}
					/>
					<TitleValue
						title="Er søknad innvilget"
						value={oversettBoolean(medl?.studieinformasjon?.soeknadInnvilget)}
					/>
					<TitleValue title="Status" value={medl?.status} kodeverk={MedlKodeverk.PERIODE_STATUS} />
					<TitleValue
						title="Statusårsak"
						value={medl?.statusaarsak}
						kodeverk={MedlKodeverk.PERIODE_ST_AARSAK}
					/>
				</BestillingData>
			</ErrorBoundary>
		</div>
	)
}
