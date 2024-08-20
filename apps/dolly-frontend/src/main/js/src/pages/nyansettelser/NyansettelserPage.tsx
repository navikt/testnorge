import React, { useState } from 'react'
import { Box, Pagination, Search, Table, VStack } from '@navikt/ds-react'
import {
	useLevendeArbeidsforholdIdentsoek,
	useLevendeArbeidsforholdLogg,
} from '@/utils/hooks/useLevendeArbeidsforhold'
import { formatDate } from '@/utils/DataFormatter'
import { ArbeidKodeverk } from '@/config/kodeverk'
import { useKodeverk } from '@/utils/hooks/useKodeverk'
import { NyansettelserSoek } from '@/pages/nyansettelser/NyansettelserSoek'
import Loading from '@/components/ui/loading/Loading'

export default () => {
	const { loggData, loading, error } = useLevendeArbeidsforholdLogg(0, 1000, 'id,DESC')

	const [identSoekData, setIdentSoekData] = useState(null)

	const [page, setPage] = useState(1)
	const rowsPerPage = 10

	const visData = () => {
		if (identSoekData) {
			return identSoekData
		} else {
			return loggData?.content
		}
	}

	let sortData = visData()
	sortData = sortData?.slice((page - 1) * rowsPerPage, page * rowsPerPage)

	const { kodeverk } = useKodeverk(ArbeidKodeverk.Arbeidsforholdstyper)

	return (
		<>
			<h1>Nyansettelser</h1>
			{/*TODO: Endre alt til ansettelser?*/}
			<VStack gap="4">
				<NyansettelserSoek setIdentSoekData={setIdentSoekData} setPage={setPage} />
				<Box background="surface-default" padding="4">
					{loading ? (
						<Loading label="Laster arbeidsforhold ..." />
					) : (
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
											<Table.Row key={`${row.id} - ${idx}`}>
												<Table.DataCell width={'15%'}>{row.folkeregisterident}</Table.DataCell>
												<Table.DataCell width={'15%'}>{row.organisasjonsnummer}</Table.DataCell>
												<Table.DataCell width={'15%'}>{formatDate(row.ansattfra)}</Table.DataCell>
												<Table.DataCell width={'35%'}>
													{kodeverk?.length > 0
														? kodeverk?.find((kode) => kode?.value === row.arbeidsforholdType)
																?.label
														: row.arbeidsforholdType}
												</Table.DataCell>
												<Table.DataCell width={'20%'}>{row.stillingsprosent}</Table.DataCell>
											</Table.Row>
										)
									})}
								</Table.Body>
							</Table>
							{loggData?.content?.length > rowsPerPage && (
								<Pagination
									page={page}
									onPageChange={setPage}
									count={Math.ceil(visData()?.length / rowsPerPage)}
									size="small"
									style={{ marginTop: '20px', justifyContent: 'center' }}
								/>
							)}
						</div>
					)}
				</Box>
			</VStack>
		</>
	)
}
