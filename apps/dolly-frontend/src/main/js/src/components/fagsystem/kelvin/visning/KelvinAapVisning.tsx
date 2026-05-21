import Loading from '@/components/ui/loading/Loading'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { Alert } from '@navikt/ds-react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { arrayToString, codeToNorskLabel, oversettBoolean, showLabel } from '@/utils/DataFormatter'
import React from 'react'
import { KelvinAapVisningTypes } from '@/components/fagsystem/kelvin/initialValues'

export const KelvinAapVisning = ({
	data,
	loading,
	harKelvinAapBestilling,
}: KelvinAapVisningTypes) => {
	if (loading) {
		return <Loading label="Laster Kelvin AAP-ytelse ..." />
	}

	if (!data && !harKelvinAapBestilling) {
		return null
	}

	const manglerFagsystemdata = harKelvinAapBestilling && !data

	return (
		<div>
			<SubOverskrift label="Kelvin AAP-ytelse" iconKind="arena" isWarning={manglerFagsystemdata} />
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke Kelvin AAP-ytelse på person
				</Alert>
			) : (
				<ErrorBoundary>
					<div className="person-visning_content">
						<TitleValue title="Saksnummer" value={data.saksnummer} />
						<TitleValue title="Behandlingsstatus" value={codeToNorskLabel(data.behandlingStatus)} />
						<TitleValue title="Ferdig" value={oversettBoolean(data.ferdig)} />
					</div>
					<h4 style={{ width: '100%', marginTop: '0' }}>Generelt</h4>
					<div className="person-visning_content">
						<TitleValue title="Er student" value={oversettBoolean(data.soeknad.erStudent)} />
						<TitleValue
							title="Har medlemskap i folketrygden"
							value={oversettBoolean(data.soeknad.harMedlemskap)}
						/>
						<TitleValue
							title="Har yrkesskade"
							value={oversettBoolean(data.soeknad.harYrkesskade)}
						/>
					</div>
					<h4 style={{ width: '100%', marginTop: '0' }}>Andre ytelser/utbetalinger (samordning)</h4>
					<div className="person-visning_content">
						<TitleValue
							title="Stønad"
							value={arrayToString(
								data.soeknad.andreUtbetalinger.stoenad?.map((stoenad) =>
									showLabel('kelvinAapStoenad', stoenad),
								),
							)}
						/>
						<TitleValue
							title="Hvem betaler (AFP)"
							value={data.soeknad.andreUtbetalinger.afp?.hvemBetaler}
						/>
						<TitleValue
							title="Lønn"
							value={showLabel('jaNei', data.soeknad.andreUtbetalinger.loenn)}
						/>
					</div>
				</ErrorBoundary>
			)}
		</div>
	)
}
