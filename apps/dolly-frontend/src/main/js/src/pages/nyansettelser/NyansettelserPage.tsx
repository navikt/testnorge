import React, { useState } from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { Box, Pagination, Search, Table } from '@navikt/ds-react'

const data = [
	{
		ident: '12345678910',
		orgnr: '123456789',
		dato: '01.01.2021',
	},
	{
		ident: '12345678910',
		orgnr: '123456789',
		dato: '01.01.2021',
	},
	{
		ident: '12345678910',
		orgnr: '123456789',
		dato: '01.01.2021',
	},
	{
		ident: '12345678910',
		orgnr: '123456789',
		dato: '01.01.2021',
	},
	{
		ident: '12345678910',
		orgnr: '123456789',
		dato: '01.01.2021',
	},
	{
		ident: '12345678910',
		orgnr: '123456789',
		dato: '01.01.2021',
	},
	{
		ident: '12345678910',
		orgnr: '123456789',
		dato: '01.01.2021',
	},
	{
		ident: '12345678910',
		orgnr: '123456789',
		dato: '01.01.2021',
	},
	{
		ident: '12345678910',
		orgnr: '123456789',
		dato: '01.01.2021',
	},
	{
		ident: '12345678910',
		orgnr: '123456789',
		dato: '01.01.2021',
	},
	{
		ident: '12345678910',
		orgnr: '123456789',
		dato: '01.01.2021',
	},
	{
		ident: '12345678910',
		orgnr: '123456789',
		dato: '01.01.2021',
	},
]

export default () => {
	const [page, setPage] = useState(1)
	const rowsPerPage = 10

	let sortData = data
	sortData = sortData.slice((page - 1) * rowsPerPage, page * rowsPerPage)

	return (
		<>
			<h1>Nyansettelser</h1>
			<Box background="surface-default" padding="4">
				{/*<div className="grid gap-4">*/}
				<form role="search" style={{ marginBottom: '20px' }}>
					<Search
						label="Søk etter personident"
						// description="Her kan du søke på forskjellige ting, f.eks. søknadsskjemaer."
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
							<Table.HeaderCell>Organisasjonsnummer</Table.HeaderCell>
							<Table.HeaderCell>Ansatt dato</Table.HeaderCell>
						</Table.Header>
						<Table.Body>
							{sortData?.map((row: any, idx: number) => {
								return (
									<Table.Row key={row.ident + idx}>
										<Table.DataCell width={'35%'}>{row.ident}</Table.DataCell>
										<Table.DataCell width={'35%'}>{row.orgnr}</Table.DataCell>
										<Table.DataCell width={'30%'}>{row.dato}</Table.DataCell>
									</Table.Row>
								)
							})}
						</Table.Body>
					</Table>
					<Pagination
						page={page}
						onPageChange={setPage}
						count={Math.ceil(data.length / rowsPerPage)}
						size="small"
						style={{ marginTop: '20px' }}
					/>
				</div>
				{/*</div>*/}
			</Box>
		</>
	)
}
