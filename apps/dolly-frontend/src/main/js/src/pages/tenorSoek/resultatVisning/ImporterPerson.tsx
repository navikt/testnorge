import { useNavigate } from 'react-router-dom'
import { useNaviger } from '@/utils/hooks/useNaviger'
import { Button } from '@navikt/ds-react'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'
import { ArrowRightIcon } from '@navikt/aksel-icons'

export const ImporterPerson = ({ valgtPerson }) => {
	const navigate = useNavigate()
	const { result, loading } = useNaviger(valgtPerson)

	// TODO vis loading?
	if (result) {
		return null
	}

	const handleClick = (event) => {
		event.stopPropagation()
		navigate(`/importer`, {
			state: {
				importPersoner: [valgtPerson],
				// mal: mal,
				// gruppe: gruppe,
			},
		})
	}

	return (
		<Button
			data-cy={CypressSelector.BUTTON_VIS_I_GRUPPE}
			variant="tertiary"
			size="xsmall"
			icon={<ArrowRightIcon />}
			loading={loading}
			onClick={handleClick}
		>
			Importer til gruppe
		</Button>
	)
}
