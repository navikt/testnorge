import useSWRImmutable from 'swr/immutable'
import { fetcher } from '@/api'

export type BrregUnderstatusOption = {
	value: number
	label: string
}

export type BrregRolleOption = {
	value: string
	label: string
}

const understatusUrl = '/testnav-dolly-proxy/brregstub/api/v1/kode/understatus'
const rollerUrl = '/testnav-dolly-proxy/brregstub/api/v1/kode/roller'

export const useBrregUnderstatuser = () => {
	const { data, isLoading, error } = useSWRImmutable<Record<string, string>, Error>(
		understatusUrl,
		fetcher,
	)

	const understatuserOptions: BrregUnderstatusOption[] =
		!data || isLoading
			? []
			: Object.entries(data).map(([key, value]) => ({
					value: parseInt(key),
					label: `${key}: ${value}`,
				}))

	return { understatuserOptions, loading: isLoading, error }
}

export const useBrregRoller = () => {
	const { data, isLoading, error } = useSWRImmutable<Record<string, string>, Error>(
		rollerUrl,
		fetcher,
	)

	const rollerOptions: BrregRolleOption[] =
		!data || isLoading
			? []
			: Object.entries(data).map(([key, value]) => ({ value: key, label: value }))

	return { rollerOptions, loading: isLoading, error }
}
