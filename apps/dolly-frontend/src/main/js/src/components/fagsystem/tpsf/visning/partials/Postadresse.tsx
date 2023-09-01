import { AdresseKodeverk } from '@/config/kodeverk'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { Historikk } from '@/components/ui/historikk/Historikk'
import * as _ from 'lodash-es'

export const PostadresseVisning = ({ postadresse }) => {
	const { postLinje1, postLinje2, postLinje3, postLand } = postadresse

	return (
		<>
			<TitleValue title="Postadresse">
				<div>{postLinje1}</div>
				<div>{postLinje2}</div>
				<div>{postLinje3}</div>
			</TitleValue>
			<TitleValue title="Land" kodeverk={AdresseKodeverk.PostadresseLand} value={postLand} />
		</>
	)
}

export const Postadresse = ({ postadresse }) => {
	if (!postadresse || postadresse.length < 1) return false

	return (
		<div>
			<SubOverskrift label="Postadresse" iconKind="designsystem-postadresse" />
			<div className="person-visning_content">
				{_.isArray(postadresse) ? (
					<Historikk component={PostadresseVisning} propName="postadresse" data={postadresse} />
				) : (
					<PostadresseVisning postadresse={postadresse} />
				)}
			</div>
		</div>
	)
}
