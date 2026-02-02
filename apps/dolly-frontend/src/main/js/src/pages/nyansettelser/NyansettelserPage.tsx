import React, { useCallback, useMemo, useState } from 'react'
import { Alert, Box, Pagination, Table, VStack } from '@navikt/ds-react'
import { useLevendeArbeidsforholdLogg } from '@/utils/hooks/useLevendeArbeidsforhold'
import { formatDate } from '@/utils/DataFormatter'
import { ArbeidKodeverk } from '@/config/kodeverk'
import { useKodeverk } from '@/utils/hooks/useKodeverk'
import { NyansettelserSoek } from '@/pages/nyansettelser/NyansettelserSoek'
import { DollyCopyButton } from '@/components/ui/button/CopyButton/DollyCopyButton'
import Loading from '@/components/ui/loading/Loading'

export default () => {
	const [identSoekData, setIdentSoekData] = useState<any>(null)
	const [orgnummerSoekData, setOrgnummerSoekData] = useState<any>(null)
	const [page, setPage] = useState(1)

	const { loggData, loading, error } = useLevendeArbeidsforholdLogg(page - 1, 10, 'id')
	const { kodeverk } = useKodeverk(ArbeidKodeverk.Arbeidsforholdstyper)

	const visData = useCallback(() => {
		if (identSoekData) return identSoekData
		if (orgnummerSoekData) return orgnummerSoekData
		return loggData?.content
	}, [loggData, identSoekData, orgnummerSoekData])

	const pageCount = identSoekData || orgnummerSoekData ? 1 : loggData?.totalPages

	const sortData = useMemo(() => visData(), [visData])

	return (
		<>
			<h1>Nyansettelser</h1>
			<VStack gap="4">
				<NyansettelserSoek
					setIdentSoekData={setIdentSoekData}
					setOrgnummerSoekData={setOrgnummerSoekData}
					setPage={setPage}
				/>
				<Box background="default" padding="space-16">
					{loading ? (
						<Loading label="Laster arbeidsforhold ..." />
					) : !sortData || sortData.length < 1 ? (
						<Alert variant="info">Fant ingen arbeidsforhold</Alert>
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
									{sortData.map((row: any, idx: number) => {
										const ansattFra =
											row.ansattfra?.length === 3
												? new Date(row.ansattfra[0], row.ansattfra[1] - 1, row.ansattfra[2])
												: null
										return (
											<Table.Row key={`${row.id} - ${idx}`}>
												<Table.DataCell width="20%">
													<DollyCopyButton
														displayText={row.folkeregisterident}
														copyText={row.folkeregisterident}
														tooltipText="Kopier ident"
													/>
												</Table.DataCell>
												<Table.DataCell width="20%">
													<DollyCopyButton
														displayText={row.organisasjonsnummer}
														copyText={row.organisasjonsnummer}
														tooltipText="Kopier org.nr."
													/>
												</Table.DataCell>
												<Table.DataCell width="15%">{formatDate(ansattFra)}</Table.DataCell>
												<Table.DataCell width="30%">
													{kodeverk?.length > 0
														? kodeverk.find((kode: any) => kode.value === row.arbeidsforholdType)
																?.label
														: row.arbeidsforholdType}
												</Table.DataCell>
												<Table.DataCell width="15%">{row.stillingsprosent}</Table.DataCell>
											</Table.Row>
										)
									})}
								</Table.Body>
							</Table>
							{pageCount > 1 && (
								<Pagination
									page={page}
									onPageChange={setPage}
									count={pageCount}
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
