import SyntaxHighlighter from 'react-syntax-highlighter'
import styled from 'styled-components'

const StyledSyntaxHighlighter = styled(SyntaxHighlighter)`
	font-size: 0.9em;
	width: 100%;
`

type Props = {
	code: string
	language: string
	wrapLongLines?: boolean
}

export default ({ code, language, wrapLongLines = false }: Props) => (
	<StyledSyntaxHighlighter language={language} wrapLongLines={wrapLongLines}>
		{code}
	</StyledSyntaxHighlighter>
)
