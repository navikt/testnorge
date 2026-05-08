import Loading from '@/components/ui/loading/Loading'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { Alert } from '@navikt/ds-react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { arrayToString, codeToNorskLabel, oversettBoolean, showLabel } from '@/utils/DataFormatter'
import React from 'react'

export const KelvinAapVisning = ({ data, loading, harKelvinAapBestilling }) => {
	if (loading) {
		return <Loading label="Laster kelvin-data" />
	}
	if (!data && !harKelvinAapBestilling) {
		return null
	}

	const manglerFagsystemdata = harKelvinAapBestilling && !data

	return (
		<div>
			<SubOverskrift label="Kelvin AAP" iconKind="arena" isWarning={manglerFagsystemdata} />
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke Kelvin-data på person
				</Alert>
			) : (
				<ErrorBoundary>
					<div className="person-visning_content">
						<TitleValue title="Saksnummer" value={data.saksnummer} />
						<TitleValue title="Behandlingsstatus" value={codeToNorskLabel(data.behandlingStatus)} />
						<TitleValue title="Ferdig" value={oversettBoolean(data.ferdig)} />
					</div>
					<h4 style={{ width: '100%', marginTop: '0' }}>Søknad</h4>
					<div className="person-visning_content">
						<TitleValue
							title="Stønad"
							value={arrayToString(
								data.soeknad.andreUtbetalinger.stoenad?.map((stoenad) =>
									showLabel('kelvinAapStoenad', stoenad),
								),
							)}
							size="full-width"
						/>
					</div>
					<div className="person-visning_content">
						<TitleValue
							title="Hvem betaler"
							value={data.soeknad.andreUtbetalinger.afp?.hvemBetaler}
						/>
						<TitleValue
							title="Lønn"
							value={showLabel('jaNei', data.soeknad.andreUtbetalinger.loenn)}
						/>
						<TitleValue title="Er student" value={oversettBoolean(data.soeknad.erStudent)} />
						<TitleValue
							title="Har medlemskap"
							value={oversettBoolean(data.soeknad.harMedlemskap)}
						/>
						<TitleValue
							title="Har yrkesskade"
							value={oversettBoolean(data.soeknad.harYrkesskade)}
						/>
					</div>
				</ErrorBoundary>
			)}
		</div>
	)
}
