import React, { useEffect, useState } from 'react'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Panel from '~/components/ui/panel/Panel'
import Button from '~/components/ui/button/Button'

import { Alert, Table } from '@navikt/ds-react'
import styled from 'styled-components'
import { Mal } from '~/utils/hooks/useMaler'
import Icon from '~/components/ui/icon/Icon'
import { DollyApi } from '~/service/Api'
import { EndreMalnavn } from './EndreMalnavn'

type Props = {
	antallEgneMaler: any
	malListe: any
	searchText: string
	heading: string
	startOpen: boolean
	iconType: string
	mutate: () => void
	underRedigering: any
	setUnderRedigering: any
}

const StyledAlert = styled(Alert)`
	margin-bottom: 10px;
`

export const MalPanel = ({
	antallEgneMaler,
	malListe,
	searchText,
	heading,
	startOpen,
	iconType,
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
			erUnderRedigering.filter((number) => number !== id)
		)
	}

	const maler = malerFiltrert(malListe, searchText)

	return (
		<Panel
			heading={heading}
			startOpen={searchActive || startOpen}
			iconType={iconType}
			forceOpen={searchActive}
		>
			{antallEgneMaler > 0 &&
				(malerFiltrert(malListe, searchText).length > 0 ? (
					<ErrorBoundary>
						<Table size="small" zebraStripes style={{ marginBottom: '20px' }}>
							<Table.Header>
								<Table.Row>
									<Table.HeaderCell scope="col">Malnavn</Table.HeaderCell>
									<Table.HeaderCell align={'center'} scope="col">
										Endre navn
									</Table.HeaderCell>
									<Table.HeaderCell scope="col">Slett</Table.HeaderCell>
								</Table.Row>
							</Table.Header>
							<Table.Body>
								{maler.map(({ malNavn, id, bestilling }, idx) => {
									return (
										<Table.Row key={idx}>
											<Table.HeaderCell scope="row" style={{ width: '720px' }}>
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
														onClick={() => {
															setUnderRedigering(underRedigering.concat([id]))
														}}
													>
														<Icon kind={'edit'} />
													</Button>
												)}
											</Table.HeaderCell>
											<Table.HeaderCell>
												<Button onClick={() => slettMal(id, bestilling?.organisasjon)}>
													<Icon kind={'trashcan'} />
												</Button>
											</Table.HeaderCell>
										</Table.Row>
									)
								})}
							</Table.Body>
						</Table>
					</ErrorBoundary>
				) : (
					<StyledAlert variant={'info'}>Ingen maler samsvarte med søket ditt</StyledAlert>
				))}
		</Panel>
	)
}

const malerFiltrert = (malListe: Mal[], searchText: string) =>
	malListe.filter((mal) => mal.malNavn.toLowerCase().includes(searchText.toLowerCase()))
