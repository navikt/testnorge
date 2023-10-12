import Loading from '@/components/ui/loading/Loading'
import { useCurrentBrukerStatistikk } from '@/utils/hooks/useDollyStatistikk'

export default (brukerId: string) => {
	const { dollyStatistikk, loading } = useCurrentBrukerStatistikk(brukerId)
	if (loading) {
		return <Loading label={'Laster Statistikk...'} />
	}

	return (
		<>
			<h1>Dolly statistikk</h1>
			<h2>Antall bestillinger</h2>
			{dollyStatistikk.antallBestillinger}
		</>
	)
}
