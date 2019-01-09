import React, { Component, Fragment } from 'react'
import Loading from '~/components/loading/Loading'
import Table from '~/components/table/Table'
import ContentContainer from '~/components/contentContainer/ContentContainer'
import Formatters from '~/utils/DataFormatter'
import PersonDetaljerConnector from '../PersonDetaljer/PersonDetaljerConnector'
import PaginationConnector from '~/components/pagination/PaginationConnector'
import { TransitionGroup, CSSTransition } from 'react-transition-group'

export default class TestbrukerListe extends Component {
	componentDidMount() {
		if (this.props.testidenter && this.props.testidenter.length) {
			this.props.getTPSFTestbrukere()
		}
	}

	render() {
		const {
			isFetching,
			testidenter,
			testbrukerListe,
			headers,
			editTestbruker,
			searchActive,
			username,
			isDeleting
		} = this.props

		if (!testidenter)
			return (
				<ContentContainer>
					Trykk på opprett personer-knappen for å starte en bestilling.
				</ContentContainer>
			)

		return (
			<Fragment>
				{isFetching || !testbrukerListe ? (
					<Loading label="laster testbrukere" panel />
				) : (
					<PaginationConnector
						items={testbrukerListe}
						render={testbrukere => {
							const hei = Formatters.sort2DArray(testbrukere, 5)
							console.log(hei, 'he')
							return (
								<div className="oversikt-container">
									<Fragment>
										{testbrukere && testbrukere.length <= 0 && searchActive ? (
											<ContentContainer>Søket gav ingen resultater.</ContentContainer>
										) : (
											<Table>
												<Table.Header>
													{headers.map((header, idx) => (
														<Table.Column key={idx} width={header.width} value={header.label} />
													))}
													<Table.Column width="10" value="" />
												</Table.Header>

												<TransitionGroup component={null}>
													{testbrukere &&
														Formatters.sort2DArray(testbrukere, 5).map((bruker, idx) => {
															// Note: idx=0 of bruker (data) is parsed to be ID
															return (
																<CSSTransition
																	key={bruker[0]}
																	timeout={isDeleting ? 2000 : 1}
																	classNames="fade"
																>
																	<Table.Row
																		key={bruker[0]}
																		expandComponent={
																			<PersonDetaljerConnector
																				personId={bruker[0]}
																				username={username}
																				editAction={() => editTestbruker(bruker[0])}
																			/>
																		}
																	>
																		{bruker.map((dataCell, cellIdx) => (
																			<Table.Column
																				key={cellIdx}
																				width={headers[cellIdx].width}
																				value={dataCell}
																			/>
																		))}
																	</Table.Row>
																</CSSTransition>
															)
														})}
												</TransitionGroup>
											</Table>
										)}
									</Fragment>
								</div>
							)
						}}
					/>
				)}
			</Fragment>
		)
	}
}
