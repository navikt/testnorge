import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { Alert, Box, Button, Table } from '@navikt/ds-react'
import { Mal } from '@/utils/hooks/useMaler'
import { EndreMalnavn } from './EndreMalnavn'
import { TestComponentSelectors } from '#/mocks/Selectors'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import { PencilWritingIcon } from '@navikt/aksel-icons'
import { SlettMal } from '@/pages/minSide/maler/SlettMal'
import { initialValuesBasedOnMal } from '@/components/bestillingsveileder/options/malOptions'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import * as _ from 'lodash-es'
import { Bestillingsdata } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { BestillingsdataOrganisasjon } from '@/components/bestilling/sammendrag/BestillingsdataOrganisasjon'

type Props = {
	antallEgneMaler: any
	malListe: any
	searchText: string
	type: string
	mutate: () => void
	underRedigering: any
	setUnderRedigering: any
}

export const MalPanel = ({
	antallEgneMaler,
	malListe,
	searchText,
	type,
	mutate,
	underRedigering,
	setUnderRedigering,
}: Props) => {
	const { dollyEnvironments } = useDollyEnvironments()

	const erUnderRedigering = (id: number) => underRedigering.includes(id)

	const avsluttRedigering = (id: number) => {
		setUnderRedigering((erUnderRedigering: any[]) =>
			erUnderRedigering.filter((number) => number !== id),
		)
	}

	const harUtdaterteVerdier = (bestilling: any) => {
		if (bestilling?.aareg?.find((arbforh: any) => arbforh?.amelding?.length > 0)) {
			return 'Denne malen er utdatert, og vil ikke fungere som den skal. Dette fordi den inneholder arbeidsforhold med A-melding, som ikke lenger er støttet. Vi anbefaler at du sletter denne malen og oppretter en ny.'
		}
		if (_.has(bestilling, 'sykemelding.syntSykemelding')) {
			return 'Denne malen er utdatert, og vil ikke fungere som den skal. Dette fordi den inneholder syntetisk sykemelding, som ikke lenger er støttet. Vi anbefaler at du sletter denne malen og oppretter en ny.'
		}
		if (_.has(bestilling, 'sigrunstub')) {
			return 'Denne malen er utdatert, og vil ikke fungere som den skal. Dette fordi den inneholder sigrunstub med lignet inntekt, som ikke lenger er støttet. Vi anbefaler at du sletter denne malen og oppretter en ny.'
		}
	}

	const maler = malerFiltrert(malListe, searchText)
	console.log('maler: ', maler) //TODO - SLETT MEG

	const DataCells = ({ id, malNavn, bestilling }) => (
		<>
			<Table.DataCell scope="row" width={'75%'}>
				{erUnderRedigering(id) ? (
					<EndreMalnavn
						malNavn={malNavn}
						id={id}
						bestilling={bestilling}
						avsluttRedigering={(id: number) => {
							avsluttRedigering(id)
							mutate()
						}}
					/>
				) : (
					<span style={{ fontWeight: 'normal' }}>{malNavn}</span>
				)}
			</Table.DataCell>
			<Table.DataCell align={'center'} width={'15%'}>
				{erUnderRedigering(id) ? (
					<Button variant={'secondary'} size={'small'} onClick={() => avsluttRedigering(id)}>
						Avbryt
					</Button>
				) : (
					<Button
						data-testid={TestComponentSelectors.BUTTON_MINSIDE_ENDRE_MALNAVN}
						onClick={() => {
							setUnderRedigering(underRedigering.concat([id]))
						}}
						variant={'tertiary'}
						icon={<PencilWritingIcon />}
						size={'small'}
					/>
				)}
			</Table.DataCell>
			<Table.DataCell width={'10%'}>
				<SlettMal id={id} organisasjon={bestilling?.organisasjon} mutate={mutate} />
			</Table.DataCell>
		</>
	)

	return (
		<Box background="surface-default" padding="4">
			{antallEgneMaler > 0 ? (
				malerFiltrert(malListe, searchText).length > 0 ? (
					<ErrorBoundary>
						<Table>
							<Table.Header>
								<Table.Row>
									<Table.HeaderCell />
									<Table.HeaderCell scope="col">Malnavn</Table.HeaderCell>
									<Table.HeaderCell scope="col" align={'center'}>
										Endre navn
									</Table.HeaderCell>
									<Table.HeaderCell scope="col">Slett</Table.HeaderCell>
								</Table.Row>
							</Table.Header>
							<Table.Body>
								{maler.map(({ malNavn, id, malBestilling, bestilling }) => {
									const bestillingData = malBestilling || bestilling
									const erOrganisasjon = _.has(bestillingData, 'organisasjon')
									const alert = harUtdaterteVerdier(bestillingData)
									const erTomBestilling = isEmpty(bestillingData, [
										'id2032',
										'identtype',
										'syntetisk',
									])
									const bestillingBasedOnMal = initialValuesBasedOnMal(
										{
											bestilling: bestillingData,
										},
										dollyEnvironments,
									)
									return (
										<Table.ExpandableRow
											key={id}
											content={
												<>
													{alert && (
														<Alert
															variant={'warning'}
															size={'small'}
															style={{ marginBottom: '20px' }}
														>
															{alert}
														</Alert>
													)}
													{erTomBestilling && (
														<Alert variant={'info'} size={'small'} style={{ marginBottom: '20px' }}>
															Denne malen inneholder ingen bestillingsdata.
														</Alert>
													)}
													<div className="bestilling-data">
														{erOrganisasjon ? (
															<BestillingsdataOrganisasjon
																bestilling={bestillingData.organisasjon}
															/>
														) : (
															<Bestillingsdata bestilling={bestillingBasedOnMal} />
														)}
													</div>
												</>
											}
										>
											<DataCells id={id} bestilling={bestillingBasedOnMal} malNavn={malNavn} />
										</Table.ExpandableRow>
									)
								})}
							</Table.Body>
						</Table>
					</ErrorBoundary>
				) : (
					<StyledAlert variant={'info'}>Ingen maler samsvarte med søket ditt</StyledAlert>
				)
			) : (
				<StyledAlert variant={'info'}>
					{`Du har ingen maler for ${type} enda. Neste gang du oppretter en ny ${type} kan du lagre bestillingen
						som en mal på siste side av bestillingsveilederen.`}
				</StyledAlert>
			)}
		</Box>
	)
}

const malerFiltrert = (malListe: Mal[], searchText: string) =>
	malListe?.filter?.((mal) => mal.malNavn?.toLowerCase().includes(searchText.toLowerCase()))
