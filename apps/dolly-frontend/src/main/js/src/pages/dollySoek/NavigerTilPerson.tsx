import { Button } from '@navikt/ds-react'
import { ArrowRightIcon } from '@navikt/aksel-icons'
import { useNavigate } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { useNaviger } from '@/utils/hooks/useNaviger'

export const NavigerTilPerson = ({ ident }) => {
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

	return (
		<Button
			variant="tertiary"
			size="xsmall"
			icon={<ArrowRightIcon />}
			loading={loading}
			onClick={handleClick}
		>
			Gå til person
		</Button>
	)
}
