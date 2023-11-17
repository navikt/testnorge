import ContentContainer from '@/components/ui/contentContainer/ContentContainer'
import PersonListeConnector from '@/pages/gruppe/PersonListe/PersonListeConnector'

export const ResultatVisning = ({ resultat }) => {
	console.log('resultat: ', resultat) //TODO - SLETT MEG
	if (!resultat) {
		return <ContentContainer>Ingen søk er gjort</ContentContainer>
	}

	if (resultat?.error) {
		return <ContentContainer>Feil: {resultat.error}</ContentContainer>
	}

	if (resultat?.totalHits < 1) {
		return <ContentContainer>Ingen treff</ContentContainer>
	}

	const obj = resultat?.identer?.reduce((o, key) => ({ ...o, [key]: { ident: key } }), {})
	console.log('obj: ', obj) //TODO - SLETT MEG

	return resultat?.identer?.map((ident) => {
		console.log('ident: ', ident) //TODO - SLETT MEG
		return <p key={ident}>{ident}</p>
	})

	// <PersonListeConnector
	// 	iLaastGruppe={false}
	// 	brukertype={null}
	// 	gruppeId={null}
	// 	identer={resultat?.identer}
	// 	bestillingerById={null}
	// />
}
