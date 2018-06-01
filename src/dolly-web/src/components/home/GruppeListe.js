import React from 'react'
import GruppeListeElement from './GruppeListeElement'

export default ({ grupper }) => {
	const gruppeList = grupper.map(gruppe => (
		<GruppeListeElement key={gruppe.id} gruppeElement={gruppe} />
	))

	return <div>{gruppeList}</div>
}
