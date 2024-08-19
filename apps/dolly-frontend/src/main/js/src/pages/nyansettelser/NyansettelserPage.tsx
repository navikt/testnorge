import React, { useState } from 'react'
import { Box, Pagination, Search, Table } from '@navikt/ds-react'
import { useLevendeAnsettelseLogg } from '@/utils/hooks/useLevendeAnsettelse'
import { formatDate, showKodeverkLabel } from '@/utils/DataFormatter'
import { ArbeidKodeverk } from '@/config/kodeverk'
import { useKodeverk } from '@/utils/hooks/useKodeverk'

export default () => {
	const { loggData, loading, error } = useLevendeAnsettelseLogg(0, 1000, 'id,DESC')

	const [page, setPage] = useState(1)
	const rowsPerPage = 10

	let sortData = loggData?.content
	sortData = sortData?.slice((page - 1) * rowsPerPage, page * rowsPerPage)

	const { kodeverk } = useKodeverk(ArbeidKodeverk.Arbeidsforholdstyper)

	return (
		<>
			<h1>Nyansettelser</h1>
			{/*TODO: Endre alt til ansettelser?*/}
			<Box background="surface-default" padding="4">
				<form role="search" style={{ marginBottom: '20px' }}>
					<Search
						label="Søk etter personident"
						description="Listen nedenfor viser de siste ansettelsene. Du kan også søke etter en person ved å skrive inn personident i feltet under."
						variant="secondary"
						hideLabel={false}
						disabled={true}
						title="Funksjonalitet kommer snart"
						// size="small"
					/>
				</form>
				<div>
					<Table>
						<Table.Header>
							<Table.HeaderCell>Ident</Table.HeaderCell>
							<Table.HeaderCell>Org.nr.</Table.HeaderCell>
							<Table.HeaderCell>Ansatt fra</Table.HeaderCell>
							<Table.HeaderCell>Arbeidsforholdtype</Table.HeaderCell>
							<Table.HeaderCell>Stillingsprosent</Table.HeaderCell>
						</Table.Header>
						<Table.Body>
							{sortData?.map((row: any, idx: number) => {
								return (
									<Table.Row key={row.ident + idx}>
										<Table.DataCell width={'15%'}>{row.folkeregisterident}</Table.DataCell>
										<Table.DataCell width={'15%'}>{row.organisasjonsnummer}</Table.DataCell>
										<Table.DataCell width={'15%'}>{formatDate(row.ansattfra)}</Table.DataCell>
										<Table.DataCell width={'35%'}>
											{kodeverk?.length > 0
												? kodeverk?.find((kode) => kode?.value === row.arbeidsforholdType)?.label
												: row.arbeidsforholdType}
										</Table.DataCell>
										<Table.DataCell width={'20%'}>{row.stillingsprosent}</Table.DataCell>
									</Table.Row>
								)
							})}
						</Table.Body>
					</Table>
					<Pagination
						page={page}
						onPageChange={setPage}
						count={Math.ceil(loggData?.content?.length / rowsPerPage)}
						size="small"
						style={{ marginTop: '20px', justifyContent: 'center' }}
					/>
				</div>
			</Box>
		</>
	)
}
