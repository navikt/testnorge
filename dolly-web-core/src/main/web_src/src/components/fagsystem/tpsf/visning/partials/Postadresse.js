import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

export const Postadresse = ({ postadresse }) => {
	if (!postadresse || postadresse.length) return false

	const { postLinje1, postLinje2, postLinje3, postLand } = postadresse[0]

	return (
		<div>
			<SubOverskrift label="Postadresse" />
			<div className="person-visning_content">
				<TitleValue title="Postadresse">
					<div>{postLinje1}</div>
					<div>{postLinje2}</div>
					<div>{postLinje3}</div>
				</TitleValue>

				<TitleValue title="Land" kodeverk="Landkoder" value={postLand} />
			</div>
		</div>
	)
}
