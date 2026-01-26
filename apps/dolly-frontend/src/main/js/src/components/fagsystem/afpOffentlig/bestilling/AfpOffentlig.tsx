import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import {
	BestillingData,
	BestillingTitle,
} from '@/components/bestilling/sammendrag/Bestillingsvisning'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { showTpNavn } from '@/components/fagsystem/afpOffentlig/visning/AfpOffentligVisning'
import {
	AfpOffentligTypes,
	BeloepTypes,
	MocksvarTypes,
} from '@/components/fagsystem/afpOffentlig/afpOffentligTypes'
import { useTpOrdningKodeverk } from '@/utils/hooks/usePensjon'

type AfpOffentligProps = {
	pensjon: AfpOffentligTypes
}

export const AfpOffentlig = ({ pensjon }: AfpOffentligProps) => {
	const { tpOrdningData } = useTpOrdningKodeverk()

	if (!pensjon || isEmpty(pensjon)) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>AFP offentlig</BestillingTitle>
				{pensjon.direktekall?.length > 0 && (
					<div className="bestilling-blokk">
						<BestillingData>
							<TitleValue
								title="Direktekall"
								value={pensjon.direktekall
									?.map((tpId) => showTpNavn(tpId, tpOrdningData))
									?.join(', ')}
								size="xlarge"
							/>
						</BestillingData>
					</div>
				)}
				<DollyFieldArray header="AFP offentlig" data={pensjon?.mocksvar}>
					{(afpOffentlig: MocksvarTypes, idx: number) => (
						<React.Fragment key={idx}>
							<TitleValue title="TP-ordning" value={showTpNavn(afpOffentlig.tpId, tpOrdningData)} />
							<TitleValue
								title="Status AFP"
								value={showLabel('statusAfp', afpOffentlig.statusAfp)}
							/>
							<TitleValue title="Virkningsdato" value={formatDate(afpOffentlig.virkningsDato)} />
							<TitleValue title="Sist benyttet G" value={afpOffentlig.sistBenyttetG} />
							{afpOffentlig.belopsListe?.length > 0 && (
								<DollyFieldArray header="Beløp" data={afpOffentlig.belopsListe} nested>
									{(belop: BeloepTypes, idy: number) => (
										<React.Fragment key={idy}>
											<TitleValue title="F.o.m. dato" value={formatDate(belop.fomDato)} />
											<TitleValue title="Beløp" value={belop.belop} />
										</React.Fragment>
									)}
								</DollyFieldArray>
							)}
						</React.Fragment>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
