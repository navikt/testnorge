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
	console.log('team: ', team) //TODO - SLETT MEG
	return (
		<VStack gap="4" align="baseline">
			<LabelValue label="Hensikt" value={team.hensikt} />
			<LabelValue label="Admin" value={team.admin} />
			<LabelValue label="Medlemmer" value={team.medlemmer.join(', ')} />
		</VStack>
	)
}
