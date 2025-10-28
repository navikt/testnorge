import { useValiderIdent } from '@/utils/hooks/useIdentPool'

export default () => {
	const { validering, loading, error } = useValiderIdent('13428699909')
	console.log('validering: ', validering) //TODO - SLETT MEG
	return <h1>Valider fødselsnummer</h1>
}
