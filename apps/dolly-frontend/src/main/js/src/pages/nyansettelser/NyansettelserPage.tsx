import React, { useCallback, useEffect, useState } from 'react'
import { Alert, Box, Pagination, Table, VStack } from '@navikt/ds-react'
import { useLevendeArbeidsforholdLogg } from '@/utils/hooks/useLevendeArbeidsforhold'
import { formatDate } from '@/utils/DataFormatter'
import { ArbeidKodeverk } from '@/config/kodeverk'
import { useKodeverk } from '@/utils/hooks/useKodeverk'
import { NyansettelserSoek } from '@/pages/nyansettelser/NyansettelserSoek'
import { DollyCopyButton } from '@/components/ui/button/CopyButton/DollyCopyButton'
import Loading from '@/components/ui/loading/Loading'

export default () => {
	const [identSoekData, setIdentSoekData] = useState(null)
	const [orgnummerSoekData, setOrgnummerSoekData] = useState(null)

	const [page, setPage] = useState(1)
	const [sortData, setSortData] = useState(null)
	const { loggData, loading, error } = useLevendeArbeidsforholdLogg(page - 1, 10, 'id')
	const { kodeverk } = useKodeverk(ArbeidKodeverk.Arbeidsforholdstyper)

	useEffect(() => {
		setSortData(() => visData())
	}, [loggData])

	const visData = useCallback(() => {
		if (identSoekData) {
			return identSoekData
		} else if (orgnummerSoekData) {
			return orgnummerSoekData
		} else {
			return loggData?.content
		}
	}, [loggData, identSoekData, orgnummerSoekData])

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
									<tr>
										<Table.HeaderCell>Ident</Table.HeaderCell>
										<Table.HeaderCell>Org.nr.</Table.HeaderCell>
										<Table.HeaderCell>Ansatt fra</Table.HeaderCell>
										<Table.HeaderCell>Arbeidsforholdtype</Table.HeaderCell>
										<Table.HeaderCell>Stillingsprosent</Table.HeaderCell>
									</tr>
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
							<Pagination
								page={page}
								onPageChange={setPage}
								count={loggData?.totalPages}
								size="small"
								style={{ marginTop: '20px' }}
							/>
						</div>
					)}
				</Box>
			</VStack>
		</>
	)
}
