import useSWR from 'swr'
import useSWRImmutable from 'swr/immutable'
import { v4 as _uuid } from 'uuid'
import { fetcher } from '@/api'
import { HenvendelseListeResponse } from '@/components/fagsystem/henvendelse/HenvendelseTypes'

const baseUrl = '/testnav-dolly-proxy/henvendelse/api'
const temagrupperUrl = `${baseUrl}/henvendelse/kodeverk/temagrupper`
const getHenvendelselisteUrl = (aktorId: string) =>
	`${baseUrl}/henvendelseinfo/henvendelseliste/v2?aktorid=${aktorId}&pageSize=200`

const henvendelseFetcher = (url: string) => fetcher(url, { 'x-correlation-id': 'Dolly-' + _uuid() })

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
		henvendelseFetcher,
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

export const useHenvendelser = (aktorId?: string) => {
	const { data, isLoading, error } = useSWR<HenvendelseListeResponse, Error>(
		aktorId ? getHenvendelselisteUrl(aktorId) : null,
		henvendelseFetcher,
		{ errorRetryCount: 0, revalidateOnFocus: false },
	)

	return {
		henvendelser: data?.data,
		loading: isLoading,
		error,
	}
}
