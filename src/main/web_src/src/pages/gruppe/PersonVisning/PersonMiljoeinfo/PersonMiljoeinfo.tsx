import React from 'react'
import { useAsyncFn } from 'react-use'
import { useAsync } from 'react-use'
import { TpsfApi } from '~/service/Api'
import Loading from '~/components/ui/loading/Loading'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TpsDataVisning } from './TpsDataVisning'

export const PersonMiljoeinfo = (ident: string) => {
	if (!ident) return null

	// const [miljoeInfo, fetch] = useAsyncFn(async () => {
	// 	const { data } = await TpsfApi.hentTpsInnhold(ident)
	// 	console.log('data :>> ', data)
	// 	return data
	// }, [])
	console.log('ident :>> ', ident)

	const state = useAsync(async () => {
		const response = await TpsfApi.hentTpsInnhold(ident)
		console.log('response :>> ', response)
		return response
	}, [])

	console.log('state :>> ', state)
	// console.log('miljoeInfo.value :>> ', miljoeInfo.value)

	return (
		<div>
			<h4>Finnes i miljøer</h4>
			{state.loading && <Loading label="Laster miljøer" />}
			{state.value && <TpsDataVisning data={state.value.data} />}
			{/* {state.value && (
				<DollyFieldArray data={state.value.data}>
					{(id, idx) => <p>{id.environment}</p>}
				</DollyFieldArray>
			)} */}
			{/* {state.value &&
				state.value.data.forEach(miljoe => (
					// console.log('miljoe :>> ', miljoe)
					<p>{miljoe.environment}</p>
				))} */}
			{/* <p>{fetch()}</p> */}
			{/* <div>{miljoeInfo}</div> */}
		</div>
	)
}
