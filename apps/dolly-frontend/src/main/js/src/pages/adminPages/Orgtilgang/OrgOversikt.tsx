import { Alert, Box, Pagination, Table, VStack } from '@navikt/ds-react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import Loading from '@/components/ui/loading/Loading'
import React, { useState } from 'react'
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
	const [page, setPage] = useState(1)
	const rowsPerPage = 10

	let sortData = organisasjonTilgang
	sortData = sortData?.slice((page - 1) * rowsPerPage, page * rowsPerPage)

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
					<VStack gap="space-16">
						<Table>
							<Table.Header>
								<Table.Row>
									<Table.HeaderCell scope="col">Org.nr.</Table.HeaderCell>
									<Table.HeaderCell scope="col">Navn</Table.HeaderCell>
									<Table.HeaderCell scope="col">Form</Table.HeaderCell>
									<Table.HeaderCell scope="col">Miljø</Table.HeaderCell>
									<Table.HeaderCell scope="col">Endre org.</Table.HeaderCell>
								</Table.Row>
							</Table.Header>
							<Table.Body>
								{sortData?.map(({ organisasjonsnummer, navn, organisasjonsform, miljoe }, idx) => {
									return (
										<Table.Row key={organisasjonsnummer + idx}>
											<Table.HeaderCell scope="row">{organisasjonsnummer}</Table.HeaderCell>
											<Table.DataCell width="55%">{navn}</Table.DataCell>
											<Table.DataCell width="10%">{organisasjonsform}</Table.DataCell>
											<Table.DataCell width="10%">{miljoe}</Table.DataCell>
											<Table.DataCell width="12%">
												<DeleteOrganisasjon
													orgNr={organisasjonsnummer}
													navn={navn}
													mutate={mutate}
												/>
												<RedigerOrganisasjon
													orgNr={organisasjonsnummer}
													miljoe={miljoe}
													mutate={mutate}
												/>
											</Table.DataCell>
										</Table.Row>
									)
								})}
							</Table.Body>
						</Table>
						<Pagination
							page={page}
							onPageChange={setPage}
							count={Math.ceil(organisasjonTilgang?.length / rowsPerPage)}
							size="small"
						/>
					</VStack>
				</ErrorBoundary>
			)}
		</Box>
	)
}
