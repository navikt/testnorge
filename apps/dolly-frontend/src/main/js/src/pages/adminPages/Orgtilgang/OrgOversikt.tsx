import { Alert, Box, Table } from '@navikt/ds-react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { formatDate } from '@/utils/DataFormatter'
import Loading from '@/components/ui/loading/Loading'
import React from 'react'
import { DeleteOrganisasjon } from '@/pages/adminPages/Orgtilgang/DeleteOrganisasjon'
import { OrgtilgangTypes } from '@/pages/adminPages/Orgtilgang/OrgtilgangForm'
import { RedigerOrganisasjon } from '@/pages/adminPages/Orgtilgang/RedigerOrganisasjon'

type OversiktTypes = {
	organisasjonTilgang: Array<OrgtilgangTypes>
	loading: boolean
	error: any
	mutate: Function
}

export const OrgOversikt = ({ organisasjonTilgang, loading, error, mutate }: OversiktTypes) => {
	return (
		<Box background="surface-default" padding="4">
			<h2 style={{ marginTop: '5px' }}>Organisasjoner som har tilgang til Dolly</h2>
			{error && (
				<Alert variant={'error'} size={'small'}>
					Feil: {error.message}
				</Alert>
			)}
			{loading && (!organisasjonTilgang || organisasjonTilgang?.length === 0) && (
				<Loading label="Laster organisasjoner ..." />
			)}
			{organisasjonTilgang?.length > 0 && (
				<ErrorBoundary>
					<Table>
						<Table.Header>
							<Table.HeaderCell scope="col">Org.nr.</Table.HeaderCell>
							<Table.HeaderCell scope="col">Navn</Table.HeaderCell>
							<Table.HeaderCell scope="col">Form</Table.HeaderCell>
							<Table.HeaderCell scope="col">Miljø</Table.HeaderCell>
							<Table.HeaderCell scope="col">Gyldig til</Table.HeaderCell>
							<Table.HeaderCell scope="col">Endre org</Table.HeaderCell>
						</Table.Header>
						<Table.Body>
							{organisasjonTilgang?.map(
								({ organisasjonsnummer, navn, organisasjonsform, miljoe, gyldigTil }, idx) => {
									return (
										<Table.Row key={organisasjonsnummer + idx}>
											<Table.HeaderCell scope="row">{organisasjonsnummer}</Table.HeaderCell>
											<Table.DataCell>{navn}</Table.DataCell>
											<Table.DataCell>{organisasjonsform}</Table.DataCell>
											<Table.DataCell>{miljoe}</Table.DataCell>
											<Table.DataCell>{formatDate(gyldigTil)}</Table.DataCell>
											<Table.DataCell>
												<DeleteOrganisasjon
													orgNr={organisasjonsnummer}
													navn={navn}
													mutate={mutate}
												/>
												<RedigerOrganisasjon
													orgNr={organisasjonsnummer}
													gyldigTil={gyldigTil}
													miljoe={miljoe}
													mutate={mutate}
												/>
											</Table.DataCell>
										</Table.Row>
									)
								},
							)}
						</Table.Body>
					</Table>
				</ErrorBoundary>
			)}
		</Box>
	)
}
