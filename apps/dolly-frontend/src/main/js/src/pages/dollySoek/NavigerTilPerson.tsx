import { Button } from '@navikt/ds-react'
import { ArrowRightIcon } from '@navikt/aksel-icons'
import { useNavigate } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { useNaviger } from '@/utils/hooks/useNaviger'
import styled from 'styled-components'
import { CypressSelector } from '../../../cypress/mocks/Selectors'

const StyledButton = styled.button`
	background: none !important;
	border: none;
	padding: 0 !important;
	color: #069;
	text-decoration: underline;
	cursor: pointer;
`

export const NavigerTilPerson = ({ ident, linkTekst = null }) => {
	const navigate = useNavigate()
	const [valgtIdent, setValgtIdent] = useState(null)

	const { result, loading } = useNaviger(valgtIdent)

	useEffect(() => {
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
	}, [result])

	const handleClick = (event) => {
		event.stopPropagation()
		setValgtIdent(ident)
	}

	if (linkTekst) {
		return <StyledButton onClick={handleClick}>{linkTekst}</StyledButton>
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
			Vis i gruppe
		</Button>
	)
}
