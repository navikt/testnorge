import React, { Component, Fragment } from 'react'
import Loading from '~/components/loading/Loading'
import Table from '~/components/table/Table'
import ContentContainer from '~/components/contentContainer/ContentContainer'
import Formatters from '~/utils/DataFormatter'
import PersonDetaljer from '../PersonDetaljer/PersonDetaljer'

export default class Gruppe extends Component {
	componentDidMount() {
		if (this.props.testidenter.length) this.props.getTestbrukere()
	}

	render() {
		const { isFetching, testidenter, testbrukere, editTestbruker, searchActive } = this.props

		if (testidenter.length <= 0)
			return (
				<ContentContainer>
					Trykk på opprett personer-knappen for å starte en bestilling.
				</ContentContainer>
			)

		return (
			<div className="oversikt-container">
				<Fragment>
					{testbrukere && testbrukere.length <= 0 && searchActive ? (
						<ContentContainer>Søket gav ingen resultater.</ContentContainer>
					) : (
						<Table>
							<Table.Header>
								<Table.Column width="15" value="ID" />
								<Table.Column width="15" value="ID-type" />
								<Table.Column width="30" value="Navn" />
								<Table.Column width="20" value="Kjønn" />
								<Table.Column width="10" value="Alder" />
							</Table.Header>

							{isFetching ? (
								<Loading label="laster testbrukere" panel />
							) : (
								testbrukere &&
								testbrukere.map((bruker, idx) => {
									return (
										<Table.Row
											key={idx}
											expandComponent={<PersonDetaljer brukerData={bruker.data} />}
											// editAction={() => editTestbruker(bruker.id)}
										>
											<Table.Column width="15" value={Formatters.formatIdentNr(bruker.id)} />
											<Table.Column width="15" value={bruker.idType} />
											<Table.Column width="30" value={bruker.navn} />
											<Table.Column width="20" value={bruker.kjonn} />
											<Table.Column width="10" value={bruker.alder} />
										</Table.Row>
									)
								})
							)}
						</Table>
					)}
				</Fragment>
			</div>
		)
	}
}
