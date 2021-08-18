import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'

export default function JiraLenker({ openAm }) {
	if (!openAm) return false

	const extractRegNr = link => link.substring(28, link.length)

	return (
		<div>
			<SubOverskrift label="Jira-lenker" />
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
