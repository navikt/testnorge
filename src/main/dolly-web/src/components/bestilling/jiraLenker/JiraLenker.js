import React from 'react'
import Header from '~/components/bestilling/sammendrag/header/Header'

export default function JiraLenker({ openAm }) {
	if (!openAm) return false

	const extractRegNr = link => link.substring(28, link.length)

	return (
		<div>
			<Header label="Jira-lenker" />
			<div>
				{openAm.map((link, i) => [
					i > 0 && ', ',
					<a href={link} key={i} target="_blank">
						{extractRegNr(link)}
					</a>
				])}
			</div>
		</div>
	)
}
