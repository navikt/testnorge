import useSWRImmutable from 'swr/immutable'
import { v4 as _uuid } from 'uuid'
import { fetcher } from '@/api'

const temagrupperUrl = '/testnav-dolly-proxy/henvendelse/api/henvendelse/kodeverk/temagrupper'

type TemagruppeType = {
	kode: string
	navn: string
}

type TemagruppeOption = {
	value: string
	label: string
}

export const useHenvendelseTemagrupper = () => {
	const { data, isLoading, error } = useSWRImmutable<TemagruppeType[], Error>(
		temagrupperUrl,
		(url: string) => fetcher(url, { 'x-correlation-id': 'Dolly-' + _uuid() }),
	)

	const temagrupper: TemagruppeOption[] =
		data?.map((temagruppe) => ({
			value: temagruppe.kode,
			label: temagruppe.navn,
		})) ?? []

	return {
		temagrupper,
		loading: isLoading,
		error,
	}
}
