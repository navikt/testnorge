import React, { useEffect, useState } from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { Panel } from '@navikt/ds-react'
import { Table } from '@navikt/ds-react'
import { Mal } from '@/utils/hooks/useMaler'
import { DollyApi } from '@/service/Api'
import { EndreMalnavn } from './EndreMalnavn'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'
import Bestillingskriterier from '@/components/bestilling/sammendrag/kriterier/Bestillingskriterier'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import { Button } from '@navikt/ds-react'
import { PencilWritingIcon } from '@navikt/aksel-icons'
import { TrashIcon } from '@navikt/aksel-icons'

type Props = {
	antallEgneMaler: any
	malListe: any
	searchText: string
	type: string
	heading: string
	startOpen: boolean
	iconType: string
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
	const [searchActive, setSearchActive] = useState(false)

	useEffect(() => {
		setSearchActive(searchText?.length > 0)
	}, [searchText])

	const slettMal = (malId: number, erOrganisasjon: boolean) => {
		erOrganisasjon
			? DollyApi.slettMalOrganisasjon(malId).then(() => mutate())
			: DollyApi.slettMal(malId).then(() => mutate())
	}

	const erUnderRedigering = (id: number) => underRedigering.includes(id)

	const avsluttRedigering = (id: number) => {
		setUnderRedigering((erUnderRedigering: any[]) =>
			erUnderRedigering.filter((number) => number !== id),
		)
	}

	const maler = malerFiltrert(malListe, searchText)

	return (
		<Panel>
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
								{maler.map(({ malNavn, id, bestilling }, idx) => {
									return (
										<Table.ExpandableRow
											key={idx}
											content={<Bestillingskriterier bestilling={bestilling} erMalVisning />}
										>
											<Table.DataCell scope="row">
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
											<Table.DataCell align={'center'}>
												{erUnderRedigering(id) ? (
													<Button
														variant={'secondary'}
														size={'small'}
														onClick={() => avsluttRedigering(id)}
													>
														Avbryt
													</Button>
												) : (
													<Button
														data-cy={CypressSelector.BUTTON_MINSIDE_ENDRE_MALNAVN}
														onClick={() => {
															setUnderRedigering(underRedigering.concat([id]))
														}}
														variant={'tertiary'}
														icon={<PencilWritingIcon />}
														size={'small'}
													/>
												)}
											</Table.DataCell>
											<Table.DataCell>
												<Button
													data-cy={CypressSelector.BUTTON_MALER_SLETT}
													onClick={() => slettMal(id, bestilling?.organisasjon)}
													variant={'tertiary'}
													icon={<TrashIcon />}
													size={'small'}
												/>
											</Table.DataCell>
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
		</Panel>
	)
}

const malerFiltrert = (malListe: Mal[], searchText: string) =>
	malListe?.filter?.((mal) => mal.malNavn?.toLowerCase().includes(searchText.toLowerCase()))
