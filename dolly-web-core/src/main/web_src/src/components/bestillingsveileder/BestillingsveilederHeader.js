import React from 'react'
import { Header } from '~/components/ui/header/Header'
import Formatter from '~/utils/DataFormatter'

export const BestillingsveilederHeader = ({ antall, identtype, opprettFraIdenter, mal }) => (
	<Header>
		<div className="flexbox">
			<Header.TitleValue title="Antall" value={`${antall} ${antall > 1 ? 'personer' : 'person'}`} />
			{!opprettFraIdenter && <Header.TitleValue title="Identtype" value={identtype} />}
			{opprettFraIdenter && (
				<Header.TitleValue
					title="Opprett fra eksisterende personer"
					value={Formatter.arrayToString(opprettFraIdenter)}
				/>
			)}
			{mal && <Header.TitleValue title="Basert på mal" value={mal.malNavn} />}
		</div>
	</Header>
)
