import React from 'react'

export default function JiraLenker({ openAm }) {
	if (!openAm) return false

	const extractRegNr = link => link.substring(28, link.length)

	return (
		<div>
			<h3>Jira-lenker</h3>
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
