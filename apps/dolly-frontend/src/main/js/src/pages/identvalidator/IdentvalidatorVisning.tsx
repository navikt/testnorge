import { Alert, Box, HStack, Table } from '@navikt/ds-react'
import { formatDate, oversettBoolean } from '@/utils/DataFormatter'

interface IdentvalidatorData {
	erGyldig: boolean
	erPersonnummer2032: boolean
	erSyntetisk: boolean
	erTestnorgeIdent: boolean
	erIProd: boolean
	identtype: string
	foedselsdato: string
	kjoenn: string
	kommentar: string
	feilmelding: string
	ident: string
}

interface IdentvalidatorVisningProps {
	data: IdentvalidatorData
}

const IconComponent = ({ item }) => {
	return item.icon === 'none' ? (
		<HStack gap="space-16">
			<div style={{ width: '20px', textAlign: 'center' }}>-</div>
			{item.value}
		</HStack>
	) : (
		<Alert variant={item.icon} inline>
			{item.value}
		</Alert>
	)
}

export const IdentvalidatorVisning = ({ data }: IdentvalidatorVisningProps) => {
	if (!data) {
		return null
	}

	const getIcon = (isValid: boolean, showError = false) => {
		if (showError) {
			return isValid ? 'success' : 'error'
		}
		return isValid ? 'success' : 'none'
	}

	const mappedData = [
		{
			label: 'Er gyldig',
			value: oversettBoolean(data.erGyldig),
			icon: getIcon(data.erGyldig, true),
		},
		{
			label: 'Er i prod',
			value: oversettBoolean(data.erIProd),
			icon: data.erIProd ? 'warning' : 'none',
		},
		{
			label: 'Er ny ident (2032)',
			value: oversettBoolean(data.erPersonnummer2032),
			icon: getIcon(data.erPersonnummer2032),
		},
		{
			label: 'Er syntetisk',
			value: oversettBoolean(data.erSyntetisk),
			icon: getIcon(data.erSyntetisk),
		},
		{
			label: 'Er Testnorge-ident',
			value: oversettBoolean(data.erTestnorgeIdent),
			icon: getIcon(data.erTestnorgeIdent),
		},
		{ label: 'Identtype', value: data.identtype },
		{ label: 'Fødselsdato', value: formatDate(data.foedselsdato) },
		{ label: 'Kjønn', value: data.kjoenn },
		{ label: 'Kommentar', value: data.kommentar },
		{ label: 'Feilmelding', value: data.feilmelding },
	]

	return (
		<Box
			padding="6"
			background={data.feilmelding ? 'surface-danger-subtle' : 'surface-success-subtle'}
			borderRadius="large"
			borderWidth="2"
			borderColor={data.feilmelding ? 'border-danger' : 'border-success'}
		>
			<Table>
				<Table.Body>
					{mappedData.map((item) => {
						if (item.value === null || item.value === undefined || item.value === '') {
							return null
						}
						return (
							<Table.Row key={item.label} shadeOnHover={false}>
								<Table.DataCell width="30%">
									<strong>{item.label}</strong>
								</Table.DataCell>
								<Table.DataCell>
									{item.icon ? <IconComponent item={item} /> : item.value}
								</Table.DataCell>
							</Table.Row>
						)
					})}
				</Table.Body>
			</Table>
		</Box>
	)
}
