import { useNavEnheter } from '@/utils/hooks/useNorg2'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingData, BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, oversettBoolean, showLabel } from '@/utils/DataFormatter'
import React from 'react'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	ForventedeInntekterSokerOgEP,
	UforetrygdTypes,
} from '@/components/fagsystem/uforetrygd/UforetrygdTypes'

type UforetrygdProps = {
	pensjon: UforetrygdTypes
}

export const Uforetrygd = ({ pensjon }: UforetrygdProps) => {
	const { navEnheter } = useNavEnheter()

	if (!pensjon || isEmpty(pensjon)) {
		return null
	}

	const navEnhetLabel = navEnheter?.find(
		(enhet) => enhet.value === pensjon.navEnhetId?.toString(),
	)?.label

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Uføretrygd</BestillingTitle>
				<BestillingData>
					<TitleValue title="Uføretidspunkt" value={formatDate(pensjon?.uforetidspunkt)} />
					<TitleValue title="Krav fremsatt dato" value={formatDate(pensjon?.kravFremsattDato)} />
					<TitleValue
						title="Ønsket virkningsdato"
						value={formatDate(pensjon?.onsketVirkningsDato)}
					/>
					<TitleValue title="Inntekt før uførhet" value={pensjon?.inntektForUforhet} />
					<TitleValue title="Inntekt etter uførhet" value={pensjon?.inntektEtterUforhet} />
					<TitleValue
						title="Sats for minimum IFU"
						value={showLabel('minimumInntektForUforhetType', pensjon?.minimumInntektForUforhetType)}
					/>
					<TitleValue
						title="Uføregrad"
						value={pensjon?.uforegrad ? `${pensjon?.uforegrad}%` : null}
					/>
					<TitleValue title="Saksbehandler" value={pensjon?.saksbehandler} />
					<TitleValue title="Attesterer" value={pensjon?.attesterer} />
					<TitleValue title="NAV-kontor" value={navEnhetLabel || pensjon?.navEnhetId} />
					<TitleValue
						title="Har barnetillegg"
						value={oversettBoolean(pensjon?.barnetilleggDetaljer !== null)}
					/>
					<TitleValue
						title="Type barnetillegg"
						value={showLabel('barnetilleggType', pensjon?.barnetilleggDetaljer?.barnetilleggType)}
					/>
					{pensjon?.barnetilleggDetaljer?.forventedeInntekterSoker?.length > 0 && (
						<DollyFieldArray
							header="Forventede inntekter for søker"
							data={pensjon?.barnetilleggDetaljer?.forventedeInntekterSoker}
							nested
						>
							{(inntekt: ForventedeInntekterSokerOgEP, idx: number) => (
								<React.Fragment key={idx}>
									<TitleValue
										title="Type inntekt"
										value={showLabel('inntektType', inntekt?.inntektType)}
									/>
									<TitleValue title="Beløp" value={inntekt?.belop} />
									<TitleValue title="Dato f.o.m." value={formatDate(inntekt?.datoFom)} />
									<TitleValue title="Dato t.o.m." value={formatDate(inntekt?.datoTom)} />
								</React.Fragment>
							)}
						</DollyFieldArray>
					)}
					{pensjon?.barnetilleggDetaljer?.forventedeInntekterEP?.length > 0 && (
						<DollyFieldArray
							header="Forventede inntekter for partner"
							data={pensjon?.barnetilleggDetaljer?.forventedeInntekterEP}
							nested
						>
							{(inntekt: ForventedeInntekterSokerOgEP, idx: number) => (
								<React.Fragment key={idx}>
									<TitleValue
										title="Type inntekt"
										value={showLabel('inntektType', inntekt?.inntektType)}
									/>
									<TitleValue title="Beløp" value={inntekt?.belop} />
									<TitleValue title="Dato f.o.m." value={formatDate(inntekt?.datoFom)} />
									<TitleValue title="Dato t.o.m." value={formatDate(inntekt?.datoTom)} />
								</React.Fragment>
							)}
						</DollyFieldArray>
					)}
				</BestillingData>
			</ErrorBoundary>
		</div>
	)
}
