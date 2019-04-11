import React, { Component, Fragment } from 'react'
import Loading from '~/components/loading/Loading'
import Table from '~/components/table/Table'
import ContentContainer from '~/components/contentContainer/ContentContainer'
import Formatters from '~/utils/DataFormatter'
import PersonDetaljerConnector from '../PersonDetaljer/PersonDetaljerConnector'
import PaginationConnector from '~/components/pagination/PaginationConnector'
import { TransitionGroup, CSSTransition } from 'react-transition-group'
import { object } from 'yup';

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

		if (isFetching) {
			return <Loading label="laster testbrukere" panel />
		}

		if (!testidenter)
			return (
				<ContentContainer>
					Trykk på opprett personer-knappen for å starte en bestilling.
				</ContentContainer>
			)

		if (!testbrukerListe) return null

		const testbrukereMedEnBestillingId = Formatters.flat2DArray(testbrukerListe, 5)
		const sortedTestbrukere = Formatters.sort2DArray(testbrukereMedEnBestillingId, 5)

		return (
			<Fragment>
				{!sortedTestbrukere ? (
					<Loading label="laster testbrukere" panel />
				) : (
					<PaginationConnector
						items={sortedTestbrukere}
						render={testbrukere => {
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
														testbrukere.map((bruker, idx) => {
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
																				bestillingId={bruker[5]}
																				editAction={() => editTestbruker(bruker[0], this.dataSourceListe(testidenter, bruker[0]))}
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

	dataSourceListe = (testidenter, ident) => {
		let temp = []
		testidenter.map ( testident => {
			if (testident.ident === ident) {
				Object.keys(testident).map ( source => { 
					source.includes('Status') && temp.push(source.split('stub')[0].substr(0,4))
				})
			}
		})
		let urlString = "&tpsf"
		temp.map(source => urlString += ("&" + source))
		return urlString	
	}
}
