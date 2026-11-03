import { InfoCard, ReadMore } from '@navikt/ds-react'
import { InformationSquareIcon } from '@navikt/aksel-icons'

interface NyIdentRegelInfoProps {
	flereIdenttyper?: boolean
}

export const NyIdentRegelInfo = ({ flereIdenttyper = false }: NyIdentRegelInfoProps) => (
	<InfoCard data-color="info" size="small" style={{ marginBottom: '20px' }}>
		<InfoCard.Message icon={<InformationSquareIcon aria-hidden />}>
			Identhistorikk følger en spesifikk rekkefølge. Rekkefølgen er NPID → DNR → FNR.
			<ReadMore header="Les mer om rekkefølgen" size="small">
				Ny identitet av samme eller senere type i rekkefølgen erstatter gjeldende identitet, som
				blir identhistorikk. Ny identitet av tidligere type i rekkefølgen legges til som
				identhistorikk.
				{flereIdenttyper && ' Uten identtype får personen en ny ident av samme type.'} Syntetisk og
				ID2032 gjelder bare personer som ikke har det fra før.
			</ReadMore>
		</InfoCard.Message>
	</InfoCard>
)
