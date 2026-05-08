import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import {
	BestillingData,
	BestillingTitle,
} from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString, oversettBoolean, showLabel } from '@/utils/DataFormatter'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { KelvinAapTypes } from '@/components/fagsystem/kelvin/initialValues'

export const KelvinAap = ({ kelvinAap }: { kelvinAap: KelvinAapTypes }) => {
	if (!kelvinAap || isEmpty(kelvinAap)) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Nav AAP ytelse</BestillingTitle>
				<div className="bestilling-blokk">
					<h3>Generelt</h3>
					<BestillingData>
						<TitleValue title="Er student" value={oversettBoolean(kelvinAap.erStudent)} />
						<TitleValue
							title="Har medlemskap i folketrygden"
							value={oversettBoolean(kelvinAap.harMedlemskap)}
						/>
						<TitleValue title="Har yrkesskade" value={oversettBoolean(kelvinAap.harYrkesskade)} />
					</BestillingData>
					<h3>Andre ytelser/utbetalinger (samordning)</h3>
					<BestillingData>
						<TitleValue
							title="Stønad"
							value={arrayToString(
								kelvinAap.andreUtbetalinger.stoenad?.map((stoenad) =>
									showLabel('kelvinAapStoenad', stoenad),
								),
							)}
						/>
						<TitleValue
							title="Hvem betaler (AFP)"
							value={kelvinAap.andreUtbetalinger.afp?.hvemBetaler}
						/>
						<TitleValue
							title="Lønn"
							value={showLabel('jaNei', kelvinAap.andreUtbetalinger.loenn)}
						/>
					</BestillingData>
				</div>
			</ErrorBoundary>
		</div>
	)
}
