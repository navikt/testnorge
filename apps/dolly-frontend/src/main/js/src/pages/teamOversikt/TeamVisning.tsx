import { HStack, VStack } from '@navikt/ds-react'

const LabelValue = ({ label, value }) => {
	return (
		<HStack gap="10" justify="start">
			<h4>{label}</h4>
			<p>{value}</p>
		</HStack>
	)
}

export const TeamVisning = ({ team }) => {
	// console.log('team: ', team) //TODO - SLETT MEG
	return (
		<VStack gap="4" align="baseline">
			<LabelValue label="Beskrivelse" value={team.beskrivelse} />
			<LabelValue label="Admin" value={team.opprettetAv} />
			<LabelValue label="Medlemmer" value={team.brukere?.join(', ')} />
		</VStack>
	)
}
