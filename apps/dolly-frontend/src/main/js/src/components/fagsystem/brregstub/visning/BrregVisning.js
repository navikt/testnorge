import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { Alert } from '@navikt/ds-react'

export const BrregVisning = ({ data, loading }) => {
	if (loading) return <Loading label="Laster brreg-data" />
	if (data === undefined) return null

	const manglerFagsystemdata =
		(!data?.understatuser || data?.understatuser?.length < 1) &&
		(!data?.enheter || data?.enheter?.length < 1)

	return (
		<div>
			<SubOverskrift
				label="Brønnøysundregistrene"
				iconKind="brreg"
				isWarning={manglerFagsystemdata}
			/>
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Kunne ikke hente Brreg-data på person
				</Alert>
			) : (
				<div className="person-visning_content">
					<TitleValue
						title="Understatuser"
						value={data?.understatuser && data?.understatuser.join(', ')}
					/>
					<ErrorBoundary>
						<DollyFieldArray data={data?.enheter} header="Enhet">
							{(enhet, idx) => (
								<div className="person-visning_content" key={idx}>
									<TitleValue title="Rolle" value={enhet.rollebeskrivelse} />
									<TitleValue
										title="Registreringsdato"
										value={Formatters.formatStringDates(enhet.registreringsdato)}
									/>
									<TitleValue title="Organisasjonsnummer" value={enhet.orgNr} />
									<TitleValue title="Foretaksnavn" value={enhet.foretaksNavn.navn1} />
									{enhet.personRolle && enhet.personRolle.length > 0 && (
										<DollyFieldArray data={enhet.personRolle} header="Personroller" nested>
											{(personrolle, idx) => (
												<div className="person-visning_content" key={idx}>
													<TitleValue title="Egenskap" value={personrolle.egenskap} />
													<TitleValue
														title="Har fratrådt"
														value={Formatters.oversettBoolean(personrolle.fratraadt)}
													/>
												</div>
											)}
										</DollyFieldArray>
									)}
								</div>
							)}
						</DollyFieldArray>
					</ErrorBoundary>
				</div>
			)}
		</div>
	)
}
