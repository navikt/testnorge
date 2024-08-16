import React, { useState } from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { Box, Pagination, Search, Table } from '@navikt/ds-react'
import { useLevendeAnsettelseLogg } from '@/utils/hooks/useLevendeAnsettelse'
import { showKodeverkLabel } from '@/utils/DataFormatter'
import { ArbeidKodeverk } from '@/config/kodeverk'

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
	const { loggData, loading, error } = useLevendeAnsettelseLogg(0, 1000, 'id,DESC')

	const [page, setPage] = useState(1)
	const rowsPerPage = 10

	let sortData = loggData?.content
	sortData = sortData?.slice((page - 1) * rowsPerPage, page * rowsPerPage)

	// console.log('loggData: ', loggData) //TODO - SLETT MEG
	console.log('sortData: ', sortData) //TODO - SLETT MEG

	return (
		<>
			<h1>Nyansettelser</h1>
			{/*TODO: Endre alt til ansettelser?*/}
			<Box background="surface-default" padding="4">
				{/*<div className="grid gap-4">*/}
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
										<Table.DataCell width={'20%'}>{row.folkeregisterident}</Table.DataCell>
										<Table.DataCell width={'15%'}>{row.organisasjonsnummer}</Table.DataCell>
										<Table.DataCell width={'15%'}>{row.ansattfra}</Table.DataCell>
										<Table.DataCell width={'30%'}>
											{/*{showKodeverkLabel(*/}
											{/*	ArbeidKodeverk.Arbeidsforholdstyper,*/}
											{/*	row.arbeidsforholdType,*/}
											{/*)}*/}
											{/*TODO: Funker ikke fordi skrivefeil ordineartArbeidsfohold*/}
											{row.arbeidsforholdType}
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
				{/*</div>*/}
			</Box>
		</>
	)
}
