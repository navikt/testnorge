import React from 'react'
import Loading from '~/components/ui/loading/Loading'
import { TpsMSpraak } from '~/components/fagsystem/pdl/visning/partials/tpsMessaging/TpsMSpraak'
import { TpsMSkjerming } from '~/components/fagsystem/pdl/visning/partials/tpsMessaging/TpsMSkjerming'

type TpsMProps = {
	data: any
	loading?: boolean
}

export const TpsMPersonInfo = ({ data, loading = false }: TpsMProps) => {
	if (loading) return <Loading />
	return (
		<>
			<TpsMSkjerming data={data} />
			<TpsMSpraak data={data} />
		</>
	)
}
