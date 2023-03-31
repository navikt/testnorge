import { TitleValue } from '@/components/ui/titleValue/TitleValue'

export const Personnavn = ({ data }) => {
	if (!data) return false
	const { fornavn, mellomnavn, etternavn } = data
	return (
		<>
			<TitleValue title="Fornavn" value={fornavn} />
			<TitleValue title="Mellomnavn" value={mellomnavn} />
			<TitleValue title="Etternavn" value={etternavn} />
		</>
	)
}
