import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import {
	BestillingData,
	BestillingTitle,
} from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString, oversettBoolean, showLabel } from '@/utils/DataFormatter'

export const KelvinAap = ({ kelvinAap }) => {
	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Kelvin AAP</BestillingTitle>
				<div className="bestilling-blokk">
					<TitleValue
						title="Stønad"
						value={arrayToString(
							kelvinAap.andreUtbetalinger.stoenad?.map((stoenad) =>
								showLabel('kelvinAapStoenad', stoenad),
							),
						)}
					/>
					<BestillingData>
						<TitleValue title="Hvem betaler" value={kelvinAap.andreUtbetalinger.afp?.hvemBetaler} />
						<TitleValue
							title="Lønn"
							value={showLabel('jaNei', kelvinAap.andreUtbetalinger.loenn)}
						/>
						<TitleValue title="Er student" value={oversettBoolean(kelvinAap.erStudent)} />
						<TitleValue title="Har medlemskap" value={oversettBoolean(kelvinAap.harMedlemskap)} />
						<TitleValue title="Har yrkesskade" value={oversettBoolean(kelvinAap.harYrkesskade)} />
					</BestillingData>
				</div>
			</ErrorBoundary>
		</div>
	)
}
