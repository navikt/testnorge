import { useNavigate } from 'react-router-dom'
import { useNaviger } from '@/utils/hooks/useNaviger'
import { Button } from '@navikt/ds-react'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'
import { ArrowRightIcon } from '@navikt/aksel-icons'

export const NavigerTilPerson = ({ ident }) => {
	const navigate = useNavigate()
	const { result, loading } = useNaviger(ident)

	if (!result) {
		return null
	}

	const handleClick = (event) => {
		event.stopPropagation()
		// setValgtIdent(ident)
		if (result?.gruppe?.id && !window.location.pathname.includes(`/${result?.gruppe?.id}`)) {
			navigate(`/gruppe/${result?.gruppe?.id}`, {
				replace: true,
				state: {
					hovedperson: result.identHovedperson,
					visPerson: result.identNavigerTil,
					sidetall: result.sidetall,
				},
			})
		}
	}
	//
	// if (linkTekst) {
	//     return <StyledButton onClick={handleClick}>{linkTekst}</StyledButton>
	// }

	return (
		<Button
			data-cy={CypressSelector.BUTTON_VIS_I_GRUPPE}
			variant="tertiary"
			size="xsmall"
			icon={<ArrowRightIcon />}
			loading={loading}
			onClick={handleClick}
		>
			Vis i gruppe
		</Button>
	)
}
