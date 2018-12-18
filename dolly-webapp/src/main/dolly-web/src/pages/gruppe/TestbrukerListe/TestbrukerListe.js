import React, { Component, Fragment } from 'react'
import Loading from '~/components/loading/Loading'
import Table from '~/components/table/Table'
import ContentContainer from '~/components/contentContainer/ContentContainer'
import Formatters from '~/utils/DataFormatter'
import PersonDetaljerConnector from '../PersonDetaljer/PersonDetaljerConnector'
import PaginationConnector from '~/components/Pagination/PaginationConnector'

export default class TestbrukerListe extends Component {
	componentDidMount() {
		if (this.props.testidenter.length) {
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
			username
		} = this.props

		if (testidenter.length <= 0)
			return (
				<ContentContainer>
					Trykk på opprett personer-knappen for å starte en bestilling.
				</ContentContainer>
			)

		if (!testbrukerListe) return null

		return (
			<PaginationConnector
				items={testbrukerListe}
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
										</Table.Header>

										{isFetching ? (
											<Loading label="laster testbrukere" panel />
										) : (
											testbrukere &&
											testbrukere.map((bruker, idx) => {
												// Note: idx=0 of bruker (data) is parsed to be ID
												return (
													<Table.Row
														key={idx}
														expandComponent={
															<PersonDetaljerConnector personId={bruker[0]} username={username} />
														}
														editAction={() => editTestbruker(bruker[0])}
													>
														{bruker.map((dataCell, cellIdx) => (
															<Table.Column
																key={cellIdx}
																width={headers[cellIdx].width}
																value={dataCell}
															/>
														))}
													</Table.Row>
												)
											})
										)}
									</Table>
								)}
							</Fragment>
						</div>
					)
				}}
			/>
		)
	}
}
