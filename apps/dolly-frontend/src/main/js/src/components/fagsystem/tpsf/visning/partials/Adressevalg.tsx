import React from 'react'
import { Historikk } from '~/components/ui/historikk/Historikk'
import { Adressevisning } from '~/components/fagsystem/tpsf/visning/partials/Boadresse'
import { PostadresseVisning } from '~/components/fagsystem/tpsf/visning/partials/Postadresse'
import { isArray } from 'lodash'

export const Adressevalg = ({ data }: any) => {
	return (
		<>
			{data.boadresse &&
				(isArray(data.boadresse) ? (
					data.boadresse.length > 0 && (
						<Historikk component={Adressevisning} propName="boadresse" data={data.boadresse} />
					)
				) : (
					<Adressevisning boadresse={data.boadresse} />
				))}
			{data.postadresse &&
				(isArray(data.postadresse) ? (
					data.postadresse.length > 0 && (
						<Historikk
							component={PostadresseVisning}
							propName="postadresse"
							data={data.postadresse}
						/>
					)
				) : (
					<PostadresseVisning postadresse={data.postadresse} />
				))}
		</>
	)
}
