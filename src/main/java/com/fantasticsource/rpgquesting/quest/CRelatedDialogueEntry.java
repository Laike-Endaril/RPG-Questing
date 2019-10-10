package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.dialogue.CDialogues;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public class CRelatedDialogueEntry extends Component
{
    public CStringUTF8 dialogueName = new CStringUTF8(), relation = new CStringUTF8();
    public CInt branchIndex = new CInt();

    public CRelatedDialogueEntry()
    {
    }

    public CRelatedDialogueEntry(CDialogueBranch branch, String relation)
    {
        dialogueName.set(branch.dialogueName.value);
        branchIndex.set(CDialogues.get(dialogueName.value).branches.indexOf(branch));
        this.relation.set(relation);
    }

    @Override
    public CRelatedDialogueEntry write(ByteBuf buf)
    {
        dialogueName.write(buf);
        relation.write(buf);
        branchIndex.write(buf);

        return this;
    }

    @Override
    public CRelatedDialogueEntry read(ByteBuf buf)
    {
        dialogueName.read(buf);
        relation.read(buf);
        branchIndex.read(buf);

        return this;
    }

    @Override
    public CRelatedDialogueEntry save(OutputStream stream)
    {
        dialogueName.save(stream);
        relation.save(stream);
        branchIndex.save(stream);

        return this;
    }

    @Override
    public CRelatedDialogueEntry load(InputStream stream)
    {
        dialogueName.load(stream);
        relation.load(stream);
        branchIndex.load(stream);

        return this;
    }
}
