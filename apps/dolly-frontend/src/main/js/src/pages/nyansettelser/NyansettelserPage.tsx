import React, { useState } from 'react'
import { Alert, Box, Pagination, Table, VStack } from '@navikt/ds-react'
import { useLevendeArbeidsforholdLogg } from '@/utils/hooks/useLevendeArbeidsforhold'
import { formatDate } from '@/utils/DataFormatter'
import { ArbeidKodeverk } from '@/config/kodeverk'
import { useKodeverk } from '@/utils/hooks/useKodeverk'
import { NyansettelserSoek } from '@/pages/nyansettelser/NyansettelserSoek'
import Loading from '@/components/ui/loading/Loading'
import { DollyCopyButton } from '@/components/ui/button/CopyButton/DollyCopyButton'

export default () => {
	const { loggData, loading, error } = useLevendeArbeidsforholdLogg(0, 1000, 'id')

	const [identSoekData, setIdentSoekData] = useState(null)
	const [orgnummerSoekData, setOrgnummerSoekData] = useState(null)

	const [page, setPage] = useState(1)
	const rowsPerPage = 10

	const visData = () => {
		if (identSoekData) {
			return identSoekData
		} else if (orgnummerSoekData) {
			return orgnummerSoekData
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
			<VStack gap="4">
				<NyansettelserSoek
					setIdentSoekData={setIdentSoekData}
					setOrgnummerSoekData={setOrgnummerSoekData}
					setPage={setPage}
				/>
				<Box background="surface-default" padding="4">
					{loading ? (
						<Loading label="Laster arbeidsforhold ..." />
					) : !sortData || sortData?.length < 1 ? (
						<Alert variant={'info'}>Fant ingen arbeidsforhold</Alert>
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
												<Table.DataCell width={'20%'}>
													{
														<DollyCopyButton
															displayText={row.folkeregisterident}
															copyText={row.folkeregisterident}
															tooltipText={'Kopier ident'}
														/>
													}
												</Table.DataCell>
												<Table.DataCell width={'20%'}>
													{
														<DollyCopyButton
															displayText={row.organisasjonsnummer}
															copyText={row.organisasjonsnummer}
															tooltipText={'Kopier org.nr.'}
														/>
													}
												</Table.DataCell>
												<Table.DataCell width={'15%'}>{formatDate(row.ansattfra)}</Table.DataCell>
												<Table.DataCell width={'30%'}>
													{kodeverk?.length > 0
														? kodeverk?.find((kode: any) => kode?.value === row.arbeidsforholdType)
																?.label
														: row.arbeidsforholdType}
												</Table.DataCell>
												<Table.DataCell width={'15%'}>{row.stillingsprosent}</Table.DataCell>
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
									style={{ marginTop: '20px' }}
								/>
							)}
						</div>
					)}
				</Box>
			</VStack>
		</>
	)
}
