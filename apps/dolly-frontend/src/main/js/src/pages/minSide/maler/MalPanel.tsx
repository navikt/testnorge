import React, { useEffect, useState } from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { Panel } from '@navikt/ds-react'
import Button from '@/components/ui/button/Button'
import { Table } from '@navikt/ds-react'
import { Mal } from '@/utils/hooks/useMaler'
import Icon from '@/components/ui/icon/Icon'
import { DollyApi } from '@/service/Api'
import { EndreMalnavn } from './EndreMalnavn'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'
import Bestillingskriterier from '@/components/bestilling/sammendrag/kriterier/Bestillingskriterier'
import StyledAlert from '@/components/ui/alert/StyledAlert'

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
						<Table style={{ marginBottom: '20px' }}>
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
											<Table.HeaderCell scope="row" style={{ width: '620px' }}>
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
											</Table.HeaderCell>
											<Table.HeaderCell align={'center'}>
												{erUnderRedigering(id) ? (
													<Button className={'avbryt'} onClick={() => avsluttRedigering(id)}>
														Avbryt
													</Button>
												) : (
													<Button
														data-cy={CypressSelector.BUTTON_MINSIDE_ENDRE_MALNAVN}
														onClick={() => {
															setUnderRedigering(underRedigering.concat([id]))
														}}
													>
														<Icon kind={'edit'} />
													</Button>
												)}
											</Table.HeaderCell>
											<Table.HeaderCell>
												<Button
													data-cy={CypressSelector.BUTTON_MALER_SLETT}
													onClick={() => slettMal(id, bestilling?.organisasjon)}
												>
													<Icon kind={'trashcan'} />
												</Button>
											</Table.HeaderCell>
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
